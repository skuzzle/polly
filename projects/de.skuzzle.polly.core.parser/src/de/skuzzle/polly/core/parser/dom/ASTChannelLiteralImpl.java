package de.skuzzle.polly.core.parser.dom;

import de.skuzzle.polly.dom.ASTChannelLiteral;

public class ASTChannelLiteralImpl extends ASTStringLiteralImpl implements
        ASTChannelLiteral {

    ASTChannelLiteralImpl(String value) {
        super(value);
    }



    @Override
    public ASTChannelLiteral getOrigin() {
        return super.getOrigin().as(ASTChannelLiteral.class);
    }



    @Override
    public ASTChannelLiteral deepOrigin() {
        return super.deepOrigin().as(ASTChannelLiteral.class);
    }



    @Override
    public ASTChannelLiteralImpl copy() {
        final ASTChannelLiteralImpl copy = getNodeFactory().newChannelLiteral(
                this.getValue());
        copy.setLocation(this.getLocation());
        copy.setOrigin(this);
        return copy;
    }
}
