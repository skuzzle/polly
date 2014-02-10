package de.skuzzle.polly.core.parser.dom;

import de.skuzzle.polly.dom.ASTExpression;
import de.skuzzle.polly.dom.ASTName;
import de.skuzzle.polly.dom.ASTNodeFactory;
import de.skuzzle.polly.dom.ASTOperator;
import de.skuzzle.polly.dom.ASTOperator.OperatorKind;
import de.skuzzle.polly.dom.ASTProductExpression;


public class DefaultASTNodeFactory implements ASTNodeFactory {

    @Override
    public ASTNameImpl newName(String name) {
        return new ASTNameImpl(name);
    }

    
    
    @Override
    public ASTQualifiedNameImpl newQualifiedName(ASTName... names) {
        if (names == null) {
            throw new NullPointerException("names"); //$NON-NLS-1$
        } else if (names.length == 0) {
            throw new IllegalArgumentException("names is empty"); //$NON-NLS-1$
        }
        final ASTQualifiedNameImpl name = new ASTQualifiedNameImpl(names[0]);
        for (int i = 1; i < names.length; ++i) {
            name.addName(names[i]);
        }
        name.setLocation(null);
        return name;
    }
    
    

    @Override
    public ASTQualifiedNameImpl newQualifiedName(String... names) {
        if (names == null) {
            throw new NullPointerException("names"); //$NON-NLS-1$
        } else if (names.length == 0) {
            throw new IllegalArgumentException("names is empty"); //$NON-NLS-1$
        }
        final ASTName firstName = this.newName(names[0]);
        final ASTQualifiedNameImpl name = new ASTQualifiedNameImpl(firstName);
        for (int i = 1; i < names.length; ++i) {
            name.addName(this.newName(names[i]));
        }
        name.setLocation(null);
        return name;
    }

    
    
    @Override
    public ASTProductExpressionImpl newProduct() {
        return new ASTProductExpressionImpl();
    }
    
    
    
    @Override
    public ASTProductExpressionImpl newProduct(ASTExpression... expressions) {
        final ASTProductExpressionImpl prod = this.newProduct();
        for (final ASTExpression expr : expressions) {
            prod.addExpression(expr);
        }
        return prod;
    }
    
    
    
    @Override
    public ASTIdExpressionImpl newIdExpression(ASTName name) {
        final ASTIdExpressionImpl expr = new ASTIdExpressionImpl(name);
        return expr;
    }
    
    
    
    @Override
    public ASTCallExpressionImpl newCall(ASTExpression lhs, ASTProductExpression rhs) {
        final ASTCallExpressionImpl call = new ASTCallExpressionImpl(lhs, rhs);
        return call;
    }
    
    
    
    @Override
    public ASTBinaryExpressionImpl newBinaryExpression(ASTOperator op, ASTExpression left,
            ASTExpression right) {
        final ASTBinaryExpressionImpl exp = new ASTBinaryExpressionImpl(op, left, right);
        return exp;
    }
    
    
    
    @Override
    public ASTBracedExpressionImpl newBraced(ASTExpression exp) {
        final ASTBracedExpressionImpl braced = new ASTBracedExpressionImpl(exp);
        return braced;
    }
    
    
    
    @Override
    public ASTOperatorImpl newOperator(int type, OperatorKind kind) {
        final ASTOperatorImpl op = new ASTOperatorImpl(type, kind);
        return op;
    }
    
    
    
    @Override
    public ASTUnaryExpressionImpl newUnaryExpression(ASTOperator op, ASTExpression operand) {
        final ASTUnaryExpressionImpl exp = new ASTUnaryExpressionImpl(op, operand);
        return exp;
    }
    
    
    
    @Override
    public ASTStringLiteralImpl newStringLiteral(String value) {
        final ASTStringLiteralImpl lit = new ASTStringLiteralImpl(value);
        return lit;
    }
    
    
    
    @Override
    public ASTChannelLiteralImpl newChannelLiteral(String value) {
        final ASTChannelLiteralImpl channel = new ASTChannelLiteralImpl(value);
        return channel;
    }
    
    
    
    @Override
    public ASTParameterImpl newParameter(ASTName typeName, ASTName name) {
        final ASTParameterImpl param = new ASTParameterImpl(typeName, name);
        return param;
    }
    
    
    
    @Override
    public ASTFunctionExpressionImpl newFunction(ASTExpression body) {
        final ASTFunctionExpressionImpl fun = new ASTFunctionExpressionImpl(body);
        return fun;
    }
}
