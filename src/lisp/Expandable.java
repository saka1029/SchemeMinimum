package lisp;

public interface Expandable extends Applicable {

    Obj expand(Obj args);
    
    @Override
    default Obj apply(Obj args, Env env) {
        return expand(args).eval(env);
    }
}
