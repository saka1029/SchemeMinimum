package lisp;

import java.io.InputStreamReader;
import java.io.StringReader;

public class Lisp {

    private Lisp() {}
    
    public static final Symbol QUOTE = Symbol.of("quote");
    public static final Symbol LAMBDA = Symbol.of("lambda");
    public static final List NIL = List.NIL;
    public static final Bool TRUE = Bool.TRUE;
    public static final Bool FALSE = Bool.FALSE;

    private static final Env ENV = new Env(null);
    
    static {
        ENV.define(QUOTE, (Applicable) (args, env) -> args.car());

        ENV.define(symbol("if"), (Applicable) (args, env) ->
            args.car().eval(env) != FALSE ? args.cdr().car().eval(env)
            : args.cdr().cdr() != NIL ? args.cdr().cdr().car().eval(env)
            : FALSE
        );

        ENV.define(symbol("lambda"), (Applicable) (args, env) ->
            new Closure(args.car(), args.cdr(), env)
        );

        ENV.define(symbol("define"), (Applicable) (args, env) ->
            args.car() instanceof Pair
                ? env.define((Symbol)args.car().car(), new Closure(args.car().cdr(), args.cdr(), env))
                : env.define((Symbol)args.car(), args.cdr().car().eval(env)));

        ENV.define(symbol("set!"), (Applicable) (args, env) ->
            env.set((Symbol)args.car(), args.cdr().car().eval(env)));
    }
    
    static {
        ENV.define(symbol("car"), (Procedure) args -> args.car().car());
        ENV.define(symbol("cdr"), (Procedure) args -> args.car().cdr());
        ENV.define(symbol("cons"), (Procedure) args -> cons(args.car(), args.cdr().car()));
        ENV.define(symbol("list"), (Procedure) args -> args);
        ENV.define(symbol("eq?"), (Procedure) args -> bool(args.car() == args.cdr().car()));
        ENV.define(symbol("equal?"), (Procedure) args -> bool(args.car().equals(args.cdr().car())));
        ENV.define(symbol("pair?"), (Procedure) args -> bool(args.car() instanceof Pair));
        ENV.define(symbol("display"), (Procedure) args -> {
            for (; args instanceof Pair; args = args.cdr())
                System.out.print(args.car() + " ");
            System.out.println();
            return FALSE;
        });
    }
    
    static int compare(Obj a, Obj b) {
        return ((Comparable)a).compareTo((Comparable)b);
    }

    static {
        ENV.define(symbol("="), (Procedure) args -> bool(args.car().equals(args.cdr().car())));
        ENV.define(symbol("<"), (Procedure) args -> bool(compare(args.car(), args.cdr().car()) < 0));
        ENV.define(symbol("<="), (Procedure) args -> bool(compare(args.car(), args.cdr().car()) <= 0));
        ENV.define(symbol(">"), (Procedure) args -> bool(compare(args.car(), args.cdr().car()) > 0));
        ENV.define(symbol(">="), (Procedure) args -> bool(compare(args.car(), args.cdr().car()) >= 0));
        ENV.define(symbol("+"), (Procedure) args -> args.reduce(Num.ZERO, (a, b) -> ((Num)a).plus((Num)b)));
        ENV.define(symbol("-"), (Procedure) args -> args.reduce(Num.ZERO, (a, b) -> ((Num)a).minus((Num)b)));
        ENV.define(symbol("*"), (Procedure) args -> args.reduce(Num.ONE, (a, b) -> ((Num)a).mult((Num)b)));
        ENV.define(symbol("/"), (Procedure) args -> args.reduce(Num.ONE, (a, b) -> ((Num)a).div((Num)b)));
    }
 
    static Obj letStar(Obj vars, Obj body) {
        return vars == NIL
            ? list(cons(LAMBDA, cons(NIL, body)))
            : list(cons(LAMBDA, list(list(vars.car().car()),
                    letStar(vars.cdr(), body))),
                vars.car().cdr().car());
    }

    static {
        ENV.define(symbol("let"), (Expandable) args ->
            args.car() instanceof Pair
                ? cons(cons(LAMBDA, cons(args.car().map(x -> x.car()), args.cdr())),
                    args.car().map(x -> x.cdr().car()))
                : list(list(LAMBDA,
                    list(args.car()),
                    list(symbol("set!"), args.car(),
                        cons(LAMBDA,
                            cons(args.cdr().car().map(x -> x.car()),
                                args.cdr().cdr()))),
                    cons(args.car(), args.cdr().car().map(x -> x.cdr().car()))),
                    FALSE));

        ENV.define(symbol("let*"), (Expandable) args ->
            letStar(args.car(), args.cdr()));

        ENV.define(symbol("letrec"), (Expandable) args ->
            cons(cons(LAMBDA, cons(args.car().map(x -> x.car()),
                append(args.car().map(x -> cons(symbol("set!"), x)), args.cdr()))),
                args.car().map(x -> FALSE)));

        ENV.define(symbol("begin"), (Expandable) args -> letStar(NIL, args));
    }
    
    public static Symbol symbol(String name) {
        return Symbol.of(name);
    }
    
    public static Pair cons(Obj car, Obj cdr) {
        return new Pair(car, cdr);
    }

    public static List list(Obj... args) {
        List r = NIL;
        for (int i = args.length - 1; i >= 0; --i)
            r = new Pair(args[i], r);
        return r;
    }

    public static Bool bool(boolean value) {
        return value ? TRUE : FALSE;
    }
    
    public static Obj append(Obj... lists) {
        Pair.Builder b = Pair.builder();
        for (Obj list : lists)
            for (; list instanceof Pair; list = list.cdr())
                b.tail(list.car());
        return b.build();
    }
    
    public static Obj read(String s) {
        return new Reader(new StringReader(s)).read();
    }

    public static Obj eval(Obj obj) {
        return obj.eval(ENV);
    }
    
    public static void main(String[] args) {
        boolean redirect = System.console() == null;
        String prompt = "> ";
        Reader reader = null;
        while (true) {
            if (!redirect) {
                System.out.print(prompt);
                System.out.flush();
            }
            if (reader == null)
                reader = new Reader(new InputStreamReader(System.in));
            try {
                Obj input = reader.read();
                if (input == Reader.EOF_OBJECT || input == Symbol.of("quit"))
                    break;
                Obj evaled = eval(input);
                System.out.println(evaled);
            } catch (Exception e) {
                System.err.println("!!! " + e.getMessage());
            }
        }
    }

}
