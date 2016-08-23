package lisp;

public interface Procedure extends Applicable {

    Obj apply(Obj args);
    
    static Obj evlis(Obj args, Env env) {
        return args instanceof Pair
            ? new Pair(args.car().eval(env), evlis(args.cdr(), env))
            : List.NIL;
    }

    @Override
    default Obj apply(Obj args, Env env) {
        return apply(evlis(args, env));
    }
}
