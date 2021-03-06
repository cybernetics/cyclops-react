package com.aol.cyclops.control.monads.transformers.values;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.aol.cyclops.control.Matchable;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.control.Trampoline;
import com.aol.cyclops.types.ConvertableFunctor;
import com.aol.cyclops.types.Filterable;
import com.aol.cyclops.types.Functor;
import com.aol.cyclops.types.MonadicValue;
import com.aol.cyclops.types.applicative.Applicativable;

public interface TransformerValue<T> extends  MonadicValue<T>,
                                            Supplier<T>, 
                                            ConvertableFunctor<T>, 
                                            Filterable<T>,
                                            Applicativable<T>,
                                            Matchable.ValueAndOptionalMatcher<T>{

    public boolean isValuePresent();
    public MonadicValue<T> value();
    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Filterable#filter(java.util.function.Predicate)
     */
    @Override
    TransformerValue<T> filter(Predicate<? super T> fn) ;

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.MonadicValue#unit(java.lang.Object)
     */
    @Override
    <T> TransformerValue<T> unit(T unit);

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.MonadicValue#map(java.util.function.Function)
     */
    @Override
     <R> TransformerValue<R> map(Function<? super T, ? extends R> fn);
    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Functor#cast(java.lang.Class)
     */
    @Override
    default <U> TransformerValue<U> cast(Class<? extends U> type) {
       
        return (TransformerValue<U>)MonadicValue.super.cast(type);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Functor#peek(java.util.function.Consumer)
     */
    @Override
    default TransformerValue<T> peek(Consumer<? super T> c) {
        
        return (TransformerValue<T>)MonadicValue.super.peek(c);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Functor#trampoline(java.util.function.Function)
     */
    @Override
    default <R> TransformerValue<R> trampoline(Function<? super T, ? extends Trampoline<? extends R>> mapper) {
       
        return (TransformerValue<R>)MonadicValue.super.trampoline(mapper);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Functor#patternMatch(java.util.function.Function, java.util.function.Supplier)
     */
    @Override
    default <R> TransformerValue<R> patternMatch(Function<CheckValue1<T, R>, CheckValue1<T, R>> case1,
            Supplier<? extends R> otherwise) {
       
        return (TransformerValue<R>)MonadicValue.super.patternMatch(case1, otherwise);
    }

   
    

}
