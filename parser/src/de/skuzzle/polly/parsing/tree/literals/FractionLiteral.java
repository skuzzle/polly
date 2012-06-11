package de.skuzzle.polly.parsing.tree.literals;


public class FractionLiteral extends NumberLiteral {
    
    public final static int MAX_DECIMALS = 10;

    public static void main(String[] args) {
        FractionLiteral frac = new FractionLiteral(Math.sqrt(2));
        System.out.println(frac);
    }

    private static final long serialVersionUID = 1L;
    
    private int nominator;
    private int denominator;
    private boolean illegal;
    
    
    
    
    public FractionLiteral(double value) {
        this(value, MAX_DECIMALS);
    }
    
    
    
    public FractionLiteral(double value, int maxDecimals) {
        super(value);
        if (this.isIntegerHelper(value)) {
            this.nominator = (int) value;
            this.denominator = 1;
        } else {
            // count decimals
            int dec = 0;
            while (!isIntegerHelper(value) && dec < maxDecimals) {
                ++dec;
                value *= 10;
            }
            
            if (dec == maxDecimals) {
                this.illegal = true;
                return;
            }
            
            int nom = (int) Math.pow(10, dec);
            int ggt = this.ggt((int) value, nom);
            this.nominator = (int) value / ggt;
            this.denominator = nom / ggt;
        }
    }

    
    
    private int ggt(int a, int b) {
        while (b != 0) {
            int h = a % b;
            a = b;
            b = h;
        }
        return a;
    }
    
    
    
    @Override
    public String toString() {
        if (this.illegal) {
            return super.toString();
        }
        if (this.denominator == 1) {
            return "" + this.nominator;
        } else {
            return this.nominator + "/" + this.denominator;
        }
    }
}