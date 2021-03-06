package com.aol.cyclops.control;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.Reducer;
import com.aol.cyclops.Semigroup;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.data.collections.extensions.CollectionX;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.types.ConvertableFunctor;
import com.aol.cyclops.types.Filterable;
import com.aol.cyclops.types.MonadicValue;
import com.aol.cyclops.types.MonadicValue1;
import com.aol.cyclops.types.MonadicValue2;
import com.aol.cyclops.types.applicative.Applicativable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;


/**
 * Totally lazy more powerful general Option(al) type. Maybe is lazy like a Java 8 Stream that
 * represents 0 or 1 values rather than eager like a Java 8 Optional. map / peek/ filter and flatMap build the execution chaing,
 * but are not executed until the value inside the Maybe is required.
 * 
 * Maybe is tail recursive
 * 
 * <pre>
 * {@code 
 * @Test
    public void odd() {
        System.out.println(even(Maybe.just(200000)).get());
    }

    public Maybe<String> odd(Maybe<Integer> n) {

        return n.flatMap(x -> even(Maybe.just(x - 1)));
    }

    public Maybe<String> even(Maybe<Integer> n) {
        return n.flatMap(x -> {
            return x <= 0 ? Maybe.just("done") : odd(Maybe.just(x - 1));
        });
    }
 * 
 * }
 * </pre>
 * 
 * Maybe is a functor (map) monad (flatMap) and an applicative (ap)
 * 
 * Maybe has pattern matching built in (visit, matches, patternMatch)
 * 
 * Maybe is convertable to all cyclops-react data types.
 * 
 * 
 * @author johnmcclean
 *
 * @param <T>
 */
public interface Maybe<T> extends MonadicValue1<T>,
								Supplier<T>, 
								ConvertableFunctor<T>, 
								Filterable<T>,
								Applicativable<T>,
								Matchable.ValueAndOptionalMatcher<T>
								{

   
	
	final static Maybe EMPTY = new Nothing<>();
	
	static <T> Maybe<T> none(){
		return  EMPTY;
	}
	static <T> Maybe<T> fromIterable(Iterable<T> iterable){
        return Maybe.fromEvalOf(Eval.fromIterable(iterable));
    }
	static <T> Maybe<T> fromOptional(Optional<T> opt){
		if(opt.isPresent())
			return Maybe.of(opt.get());
		return none();
	}
	
	static <T> Maybe<T> fromEvalOf(Eval<T> eval){
		return new Just<T>(eval);
	}
	static <T> Maybe<T> just(T value){
	    return of(value);
	}
	static <T> Maybe<T> of(T value){
		Objects.requireNonNull(value);
		return new Just<T>(Eval.later(()->value));
	}
	
	static <T> Maybe<T> ofNullable(T value){
	
		if(value!=null)
			return of(value);
		return none();	
	}

	static <T> Maybe<T> narrow(Maybe<? extends T> broad){
		return (Maybe<T>)broad;
	}
	
	public static <T> Maybe<ListX<T>> sequence(CollectionX<Maybe<T>> maybes){
		return AnyM.sequence(AnyM.<T>listFromMaybe(maybes)).unwrap();
	}
	
	public static <T,R> Maybe<R> accumulateJust(CollectionX<Maybe<T>> maybes,Reducer<R> reducer){
		return sequence(maybes).map(s->s.mapReduce(reducer));
	}
	public static <T,R> Maybe<R> accumulateJust(CollectionX<Maybe<T>> maybes,Function<? super T, R> mapper,Semigroup<R> reducer){
		return sequence(maybes).map(s->s.map(mapper).reduce(reducer.reducer()).get());
	}
	public static <T> Maybe<T> accumulateJust(CollectionX<Maybe<T>> maybes,Semigroup<T> reducer){
		return sequence(maybes).map(s->s.reduce(reducer.reducer()).get());
	}
	default <T> Maybe<T> unit(T unit){
		return  Maybe.of(unit);
	}
	
	
	/* (non-Javadoc)
     * @see com.aol.cyclops.types.MonadicValue#coflatMap(java.util.function.Function)
     */
    @Override
    default <R> Maybe<R> coflatMap(Function<? super MonadicValue<T>, R> mapper) {
        return (Maybe<R>)MonadicValue1.super.coflatMap(mapper);
    }
  
    /* cojoin
     * (non-Javadoc)
     * @see com.aol.cyclops.types.MonadicValue#nest()
     */
    @Override
    default  Maybe<MonadicValue<T>> nest(){
        return (Maybe<MonadicValue<T>>)MonadicValue1.super.nest();
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.types.MonadicValue2#combine(com.aol.cyclops.Monoid, com.aol.cyclops.types.MonadicValue2)
     */
    @Override
    default Maybe<T> combine(Monoid<T> monoid, MonadicValue<? extends T> v2){
        return (Maybe<T>)MonadicValue1.super.combine(monoid,v2);
    }

    

    /* (non-Javadoc)
	 * @see com.aol.cyclops.value.Value#toMaybe()
	 */
	@Override
	default Maybe<T> toMaybe() {
		return this;
	}

	
	boolean isPresent();
	
	Maybe<T> recover(Supplier<T> value);
	Maybe<T> recover(T value);
	<R> Maybe<R> map(Function<? super T, ? extends R> mapper);
	<R>  Maybe<R> flatMap(Function<? super T, ? extends MonadicValue<? extends R>> mapper);
	<R> R visit(Function<? super T,? extends R> some, 
						Supplier<? extends R> none);
	
	
	
	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Filterable#filter(java.util.function.Predicate)
	 */
	@Override
	Maybe<T> filter(Predicate<? super T> fn);

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Filterable#ofType(java.lang.Class)
	 */
	@Override
	default <U> Maybe<U> ofType(Class<? extends U> type) {
		
		return (Maybe<U>)Filterable.super.ofType(type);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Filterable#filterNot(java.util.function.Predicate)
	 */
	@Override
	default Maybe<T> filterNot(Predicate<? super T> fn) {
		
		return (Maybe<T>)Filterable.super.filterNot(fn);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Filterable#notNull()
	 */
	@Override
	default Maybe<T> notNull() {
		
		return (Maybe<T>)Filterable.super.notNull();
	}

	


	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Functor#cast(java.lang.Class)
	 */
	@Override
	default <U> Maybe<U> cast(Class<? extends U> type) {
		
		return (Maybe<U>)Applicativable.super.cast(type);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Functor#peek(java.util.function.Consumer)
	 */
	@Override
	default Maybe<T> peek(Consumer<? super T> c) {
		
		return (Maybe<T>)Applicativable.super.peek(c);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Functor#trampoline(java.util.function.Function)
	 */
	@Override
	default <R> Maybe<R> trampoline(Function<? super T, ? extends Trampoline<? extends R>> mapper) {
		
		return (Maybe<R>)Applicativable.super.trampoline(mapper);
	}
	@Override
	default <R> Maybe<R> patternMatch(
			Function<CheckValue1<T, R>, CheckValue1<T, R>> case1,Supplier<? extends R> otherwise) {
		
		return (Maybe<R>)Applicativable.super.patternMatch(case1,otherwise);
	}




	




    @AllArgsConstructor(access=AccessLevel.PRIVATE)
	public static final class Just<T> implements Maybe<T>{
		
		private final Eval<T> lazy;
		
		
		public <R> Maybe<R> map(Function<? super T, ? extends R> mapper){
			return new Just<>(lazy.map(t->mapper.apply(t)));
		}
		public <R>  Maybe<R> flatMap(Function<? super T, ? extends MonadicValue<? extends R>> mapper){
			return new Lazy(Eval.later(()->this)).flatMap(mapper);
			
		}
		public Maybe<T> filter(Predicate<? super T> test){
			if(test.test(lazy.get()))
				return this;
			return EMPTY;
		}
		public <R> R visit(Function<? super T,? extends R> some, 
				Supplier<? extends R> none){
			return map(some).get();
		}
		public Maybe<T> recover(T value){
			return this;
		}
		public Maybe<T> recover(Supplier<T> value){
			return this;
		}
		public String toString(){
			return mkString();
		}
		public T  get(){
			return lazy.get();
		}
		public boolean isPresent(){
			return true;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return Objects.hashCode(lazy.get());
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Just)
				return Objects.equals(lazy.get(),((Just)obj).get());
			else if(obj instanceof Lazy){
			    return Objects.equals(get(),((Lazy)obj).get());
			}
			return false;
		}
		
	}
    @AllArgsConstructor(access=AccessLevel.PRIVATE)
    static final class Lazy<T> implements Maybe<T>{
        
        private final Eval<Maybe<T>> lazy;
        
        
        public <R> Maybe<R> map(Function<? super T, ? extends R> mapper){
            return flatMap(t->Maybe.just(mapper.apply(t)));
        }
        public <R>  Maybe<R> flatMap(Function<? super T, ? extends MonadicValue<? extends R>> mapper){
            Supplier<Eval<Maybe<R>>> s = ()->Eval.later(()->Maybe.none()) ;
            Eval<Maybe<R>> eval = lazy.get().visit(some->Eval.later( ()->narrow(mapper.apply(some).toMaybe())), s);
            return new Lazy<R>(eval);
            
        }
        public Maybe<T> filter(Predicate<? super T> test){
           return flatMap(t-> test.test(t) ? this : Maybe.none());
        }
        public <R> R visit(Function<? super T,? extends R> some, 
                Supplier<? extends R> none){
            Maybe<R> mapped = map(some);
            return mapped.orElseGet(()->none.get());
        }
        public Maybe<T> recover(T value){
            return new Lazy<T>(lazy.map(m->m.recover(value)));
        }
        public Maybe<T> recover(Supplier<T> value){
            return new Lazy<T>(lazy.map(m->m.recover(value)));
        }
        public String toString(){
            Maybe<T> maybe =  lazy.get();
            while(maybe instanceof Lazy){
                maybe = ((Lazy<T>)maybe).lazy.get();
            }
            return maybe.mkString();
        }
        public T  get(){
            Maybe<T> maybe =  lazy.get();
            while(maybe instanceof Lazy){
                maybe = ((Lazy<T>)maybe).lazy.get();
            }
            return maybe.get();
        }
        public boolean isPresent(){
            Maybe<T> maybe =  lazy.get();
            while(maybe instanceof Lazy){
                maybe = ((Lazy<T>)maybe).lazy.get();
            }
            return maybe.isPresent();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            Maybe<T> maybe =  lazy.get();
            while(maybe instanceof Lazy){
                maybe = ((Lazy<T>)maybe).lazy.get();
            }
            return Objects.hashCode(maybe.get());
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            
            if(obj instanceof Just)
                return Objects.equals(get(),((Just)obj).get());
            if(obj instanceof Lazy)
                return Objects.equals(get(),((Lazy)obj).get());
            return false;
        }
        
    }
	
	public static class Nothing<T> implements Maybe<T>{
		
		public <R> Maybe<R> map(Function<? super T, ? extends R> mapper){
			return EMPTY;
		}
		public <R>  Maybe<R> flatMap(Function<? super T, ? extends MonadicValue<? extends R>> mapper){
			return EMPTY;
			
		}
		public Maybe<T> filter(Predicate<? super T> test){
			return EMPTY;
		}
		public T  get(){
			return  Optional.<T>ofNullable(null).get();
		}
		public Maybe<T> recover(T value){
			return Maybe.of(value);
		}
		public Maybe<T> recover(Supplier<T> value){
			return new Just<>(Eval.later(value));
		}
		public <R> R visit(Function<? super T,? extends R> some, 
				Supplier<? extends R> none){
			return none.get();
		}
		public Optional<T> toOptional(){
			return Optional.ofNullable(null);
		}
		public String toString(){
			return mkString();
		}
		public boolean isPresent(){
			return false;
		}
		
		
	}
	
}
