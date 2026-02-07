import java.util.*;

public class LinealDescendantRule extends Rule {
    
    public LinealDescendantRule(){
        setRuleText("Make 'Son' and 'Daughter' Equivalent");
    }
    
    boolean activeRule = false;
    
    Hashtable rewriteProd = new Hashtable();
    
    public AlgebraPath rewriteProduct(String theProd){
        return (AlgebraPath)rewriteProd.get(theProd);
    }
    
    public void setActiveRule(boolean flag) {
        activeRule = flag;
    }
    
    public boolean getActiveRule(){
        return activeRule;
    }
    
    
    /* rule applies if a is of form generator of term = product where
        * generator is down arrow and generation of term <= 0 or
        * generator is up arrow and term is of form XXX and generation of product <= 0
        * @a CayleyInfo the path to be tested for applicability of rule
        * return boolean
        */
    public boolean doesRuleApply(CayleyInfo a) {
        //System.out.println("In LinealDescendantRule");
        if (!activeRule) return false;
        if (a.product.isEquivalentPath()||a.product.isReducedEquivalentPath()) return false;//dwr 8/5
        //if (a.product.isEquivalentPath()) return false;//dwr 8/5
        AlgebraSymbol as = a.generator;
        if (as.getArrowType() == Bops.DOWN && !as.getSex().equals("N")) {
            AlgebraSymbolVector asv = a.term.getReducedProductPath();
            //  System.out.println("ASV "+asv+ " GEN "+asv.getGeneration());
            return (asv.getGeneration() <= 0);
        }
        if (as.getArrowType() == Bops.UP && !as.getSex().equals("N")) {
            AlgebraSymbolVector asv = a.product.getReducedProductPath();
            //System.out.println(" asv "+asv +" generation "+ asv.getGeneration());
            return (( a.term.reducedPath.sameElements()) && (asv.getGeneration() <= 0));
        }
        return false;
    }
    
    /* rule modifies CayleyTable so that So and Da of a zero or negative generation
        * element is mapped to the same element; has the effect of erasing the
        * distinction between So and Da
        * @a path to which the rule is to be applied
        * return boolean
        */
    public boolean applyRule(CayleyInfo a) {
        //System.out.println("IN LINEAL DESCENDANT RULE "+a);
        String aString = a.toString();
        AlgebraSymbol as = a.generator.getOppositeSexGenerator();
        AlgebraPath prod1 = (AlgebraPath) a.term.clone();
        AlgebraPath prod = (AlgebraPath) a.term.clone();
        AlgebraSymbolVector asv = (AlgebraSymbolVector)a.term.reducedPath.clone();
        if ((asv.getGeneration() == 0) && (asv.size() > 1)){
            //System.out.println("prod1 reduced "+prod1.reducedPath+ " as "+as);
            prod1.reducedPath.addToEnd(as);
            prod1.path.addToEnd(as);
            //System.out.println("prod reduced "+prod.reducedPath+ " gen "+a.generator);
            prod.reducedPath.addToEnd(a.generator);
            prod.path.addToEnd(a.generator);
        } else {
            //System.out.println(" else prod1 reduced "+prod1.getReducedProductPath()+ " as "+as);
            prod1.reducedPath = addEndElement(prod1.getReducedProductPath(),as);//put procedure in asv?
                                                                                // prod1.getReducedProductPath().addToEnd(as);
                                                                                //System.out.println("did prod1 "+prod1+" reduced path "+prod1.reducedPath+" get "+prod1.getReducedProductPath());
            prod1.reducePath(prod1.getReducedProductPath());
            if (prod1.reducedPath.equivalentProduct() &&
                prod1.reducedPath.equivalentLeftProduct().equals(prod1.reducedPath.equivalentRightProduct())){
                prod1.setReducedProductPath(prod1.reducedPath.equivalentLeftProduct());
            }
            //System.out.println(" else prod1 next "+prod1);
            prod = a.product;
            //System.out.println(" else prod "+prod);
        }
        if (prod.equals(prod1)) return false;
        boolean done = false;
        if (prod.reducedPath.equivalentProduct()){
            if (prod.reducedPath.equivalentLeftProduct().equals(prod1.reducedPath) ||
                prod.reducedPath.equivalentLeftProduct().equals(prod1.reducedPath)){
                rewriteProd.put(a.product.reducedPath.toString(),prod.reducedPath);
                a.product.reducedPath = prod.reducedPath;
                done = true;
            }else return false;//error prod1 not subsumed in prod
        }
        if (!prod.getReducedProductPath().sameArrowPattern(prod1.getReducedProductPath()))
            return false;
        if (!done) {
            boolean startFlag = prod.isReducedPath();
            //System.out.println("xxxxxxxxxxxxxprod "+prod+" prod1 "+prod1);
            if (startFlag && prod1.isReducedPath())
                startFlag = ((AlgebraSymbol)prod.reducedPath.elementAt(0)).getSex().equals("F");
            if (startFlag) {
                rewriteProd.put(a.product.toString(),prod1.makeEquivalentPath(prod));
                a.product = prod1.makeEquivalentPath(prod);
            } else {
                rewriteProd.put(a.product.toString(),prod.makeEquivalentPath(prod1));
                a.product = prod.makeEquivalentPath(prod1);
            }
            if (a.product.toString().equals("F&M")||a.product.toString().equals("M&F")) {
                AlgebraSymbol id = Algebra.getCurrent().getIdentityElement();
                if (!id.getValue().equals("")){
                    rewriteProd.put(a.product.toString(),new AlgebraPath(id));
                    //System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx F&M "+a.product.reducedPath.toString()+ " path "+a.product.path.toString()+" id "+id);
                    a.product.reducedPath.removeAllElements();
                    a.product.reducedPath.addElement(id);
                    //System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxEND F&M "+a.product.reducedPath.toString()+ " path "+a.product.path.toString()+" id "+id);
                }
            }
        }
        //                                                             System.out.println("THE PATH "+a.product+" the gen"+as);
        //return true;
        return (!aString.equals(a.toString()));
    }
    
    AlgebraSymbolVector addEndElement(AlgebraSymbolVector asv, AlgebraSymbol as){
        AlgebraSymbolVector ret = new AlgebraSymbolVector();
        if (!asv.equivalentProduct()) {
            asv.addToEnd(as);
            return asv;
        }
        for (asv.reset();asv.isNext();){
            AlgebraSymbol a = asv.getNext();
            if (a.getValue().equals("&")) ret.addToEnd(as);
            ret.addToEnd(a);
        }
        ret.addToEnd(as);
        return ret;
    }
}

