package lisp;

import static lisp.Lisp.*;

public class Closure implements Procedure {
    
    private Obj parms;
    private Obj body;
    private Env env;
    
    Closure(Obj parms, Obj body, Env env) {
        this.parms = parms;
        this.body = body;
        this.env = env;
    }
    
    static void pairlis(Obj parms, Obj args, Env env) {
        for (; parms instanceof Pair; parms = parms.cdr(), args = args.cdr())
            env.define((Symbol)parms.car(), args.car());
        if (parms != NIL)
            env.define((Symbol)parms, args);
    }

    static Obj progn(Obj body, Env env) {
        if (body.cdr() == NIL)
            return body.car().eval(env);
        body.car().eval(env);
        return progn(body.cdr(), env);
    }

    @Override
    public Obj apply(Obj args) {
        Env n = new Env(env);
        pairlis(parms, args, n);
        return progn(body, n);
    }
    
    @Override
    public String toString() {
        return cons(symbol("closure"), cons(parms, body)).toString();
    }
}
