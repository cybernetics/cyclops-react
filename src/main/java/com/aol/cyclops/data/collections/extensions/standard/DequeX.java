package com.aol.cyclops.data.collections.extensions.standard;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.reactivestreams.Publisher;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.control.Trampoline;
import com.aol.cyclops.util.stream.StreamUtils;

public interface DequeX<T> extends Deque<T>, MutableCollectionX<T> {
	
	static <T> Collector<T,?,Deque<T>> defaultCollector(){
		return Collectors.toCollection(()-> new ArrayDeque<>());
	}
	
	
	public static <T> DequeX<T> empty(){
		return fromIterable((Deque<T>) defaultCollector().supplier().get());
	}
	public static <T> DequeX<T> of(T...values){
		Deque<T> res = (Deque<T>) defaultCollector().supplier().get();
		for(T v: values)
			res.add(v);
		return  fromIterable(res);
	}
	public static <T> DequeX<T> singleton(T value){
		return of(value);
	}
    /**
     * Construct a DequeX from an Publisher
     * 
     * @param publisher
     *            to construct DequeX from
     * @return DequeX
     */
    public static <T> DequeX<T> fromPublisher(Publisher<? extends T> publisher) {
        return ReactiveSeq.fromPublisher((Publisher<T>)publisher).toDequeX();
    }
	public static <T> DequeX<T> fromIterable(Iterable<T> it){
		return fromIterable(defaultCollector(),it);
	}
	public static <T> DequeX<T> fromIterable(Collector<T,?,Deque<T>>  collector,Iterable<T> it){
		if(it instanceof DequeX)
			return (DequeX)it;
		if(it instanceof Deque)
			return new DequeXImpl<T>( (Deque)it, collector);
		return new DequeXImpl<T>(StreamUtils.stream(it).collect(collector),collector);
	}
	
	/* (non-Javadoc)
	 * @see com.aol.cyclops.sequence.traits.ConvertableSequence#toListX()
	 */
	@Override
	default DequeX<T> toDequeX() {
		return this;
	}
	public <T> Collector<T,?,Deque<T>> getCollector();
	
	default <T1> DequeX<T1> from(Collection<T1> c){
		return DequeX.<T1>fromIterable(getCollector(),c);
	}
	
	default <X> DequeX<X> fromStream(Stream<X> stream){
		return new DequeXImpl<>(stream.collect(getCollector()),getCollector());
	}

    /**
     * Combine two adjacent elements in a DequeX using the supplied BinaryOperator
     * This is a stateful grouping & reduction operation. The output of a combination may in turn be combined
     * with it's neighbor
     * <pre>
     * {@code 
     *  DequeX.of(1,1,2,3)
                   .combine((a, b)->a.equals(b),Semigroups.intSum)
                   .toListX()
                   
     *  //ListX(3,4) 
     * }</pre>
     * 
     * @param predicate Test to see if two neighbors should be joined
     * @param op Reducer to combine neighbors
     * @return Combined / Partially Reduced DequeX
     */
	default DequeX<T> combine(BiPredicate<? super T, ? super T> predicate, BinaryOperator<T> op){
        return (DequeX<T>)MutableCollectionX.super.combine(predicate,op);
    }
	
	@Override
	default<R> DequeX<R> unit(Collection<R> col){
		return fromIterable(col);
	}
	@Override
	default <R> DequeX<R> unit(R value){
		return singleton(value);
	}
	
	default <R> DequeX<R> unitIterator(Iterator<R> it){
		return fromIterable(()->it);
	}
	@Override
	default ReactiveSeq<T> stream(){
		
		return ReactiveSeq.fromIterable(this);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#reverse()
	 */
	@Override
	default DequeX<T> reverse() {
		
		return ( DequeX<T>)MutableCollectionX.super.reverse();
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#filter(java.util.function.Predicate)
	 */
	@Override
	default DequeX<T> filter(Predicate<? super T> pred) {
		
		return (DequeX<T>)MutableCollectionX.super.filter(pred);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#map(java.util.function.Function)
	 */
	@Override
	default <R> DequeX<R> map(Function<? super T, ? extends R> mapper) {
		
		return (DequeX<R>)MutableCollectionX.super.map(mapper);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#flatMap(java.util.function.Function)
	 */
	@Override
	default <R> DequeX<R> flatMap(Function<? super T, ? extends Iterable<? extends R>> mapper) {
	
		return (DequeX<R>)MutableCollectionX.super.flatMap(mapper);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#limit(long)
	 */
	@Override
	default DequeX<T> limit(long num) {
		
		return (DequeX<T>)MutableCollectionX.super.limit(num);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#skip(long)
	 */
	@Override
	default DequeX<T> skip(long num) {
		
		return (DequeX<T>)MutableCollectionX.super.skip(num);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#takeWhile(java.util.function.Predicate)
	 */
	@Override
	default DequeX<T> takeWhile(Predicate<? super T> p) {
		
		return (DequeX<T>)MutableCollectionX.super.takeWhile(p);
	}
	default  DequeX<T> takeRight(int num){
		return (DequeX<T>)MutableCollectionX.super.takeRight(num);
	}
	default  DequeX<T> dropRight(int num){
		return  (DequeX<T>)MutableCollectionX.super.dropRight(num);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#dropWhile(java.util.function.Predicate)
	 */
	@Override
	default DequeX<T> dropWhile(Predicate<? super T> p) {
		
		return (DequeX<T>)MutableCollectionX.super.dropWhile(p);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#takeUntil(java.util.function.Predicate)
	 */
	@Override
	default DequeX<T> takeUntil(Predicate<? super T> p) {
		
		return (DequeX<T>)MutableCollectionX.super.takeUntil(p);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#dropUntil(java.util.function.Predicate)
	 */
	@Override
	default DequeX<T> dropUntil(Predicate<? super T> p) {
		return (DequeX<T>)MutableCollectionX.super.dropUntil(p);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#trampoline(java.util.function.Function)
	 */
	@SuppressWarnings("unchecked")
	@Override
	default <R> DequeX<R> trampoline(Function<? super T, ? extends Trampoline<? extends R>> mapper) {
		return (DequeX<R>)MutableCollectionX.super.trampoline(mapper);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#slice(long, long)
	 */
	@Override
	default DequeX<T> slice(long from, long to) {
		return (DequeX<T>)MutableCollectionX.super.slice(from, to);
	}


	default DequeX<ListX<T>> grouped(int groupSize){
		return (DequeX<ListX<T>>)MutableCollectionX.super.grouped(groupSize); 
	}
	default <K, A, D> DequeX<Tuple2<K, D>> grouped(Function<? super T, ? extends K> classifier, Collector<? super T, A, D> downstream){
		return (DequeX)MutableCollectionX.super.grouped(classifier,downstream);
	}
	default <K> DequeX<Tuple2<K, Seq<T>>> grouped(Function<? super T, ? extends K> classifier){
		return (DequeX)MutableCollectionX.super.grouped(classifier);	 
	}
	default <U> DequeX<Tuple2<T, U>> zip(Iterable<? extends U> other){
		return (DequeX)MutableCollectionX.super.zip(other);
	}
	
	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#zip(java.lang.Iterable, java.util.function.BiFunction)
	 */
	@Override
	default <U, R> DequeX<R> zip(Iterable<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
		
		return (DequeX<R>)MutableCollectionX.super.zip(other, zipper);
	}


	default DequeX<ListX<T>> sliding(int windowSize){
		return (DequeX<ListX<T>>)MutableCollectionX.super.sliding(windowSize); 
	}
	default DequeX<ListX<T>> sliding(int windowSize, int increment){
		return (DequeX<ListX<T>>)MutableCollectionX.super.sliding(windowSize,increment); 
	}
	default DequeX<T> scanLeft(Monoid<T> monoid){
		return (DequeX<T>)MutableCollectionX.super.scanLeft(monoid); 
	}
	default <U> DequeX<U> scanLeft(U seed, BiFunction<? super U, ? super T, ? extends U> function){
		return (DequeX<U>)MutableCollectionX.super.scanLeft(seed,function); 	
	}
	default DequeX<T> scanRight(Monoid<T> monoid){
		return (DequeX<T>)MutableCollectionX.super.scanRight(monoid); 
	}
	default <U> DequeX<U> scanRight(U identity, BiFunction<? super T, ? super U,? extends U> combiner){
		return (DequeX<U>)MutableCollectionX.super.scanRight(identity,combiner); 
	}
	
	
	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#sorted(java.util.function.Function)
	 */
	@Override
	default <U extends Comparable<? super U>> DequeX<T> sorted(Function<? super T, ? extends U> function) {
		
		return (DequeX<T>)MutableCollectionX.super.sorted(function);
	}
	default DequeX<T> plus(T e){
		add(e);
		return this;
	}
	
	default DequeX<T> plusAll(Collection<? extends T> list){
		addAll(list);
		return this;
	}
	
	default DequeX<T> minus(Object e){
		remove(e);
		return this;
	}
	
	default DequeX<T> minusAll(Collection<?> list){
		removeAll(list);
		return this;
	}


	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.CollectionX#peek(java.util.function.Consumer)
	 */
	@Override
	default DequeX<T> peek(Consumer<? super T> c) {
		return (DequeX<T>)MutableCollectionX.super.peek(c);
	}





	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.CollectionX#patternMatch(java.lang.Object, java.util.function.Function)
	 */
	@Override
	default <R> DequeX<R> patternMatch(
			Function<CheckValue1<T, R>, CheckValue1<T, R>> case1,Supplier<? extends R> otherwise) {
		return (DequeX<R>)MutableCollectionX.super.patternMatch(case1,otherwise);
	}

	
	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#cycle(int)
	 */
	@Override
	default DequeX<T> cycle(int times) {
	
		return (DequeX<T>)MutableCollectionX.super.cycle(times);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#cycle(com.aol.cyclops.sequence.Monoid, int)
	 */
	@Override
	default DequeX<T> cycle(Monoid<T> m, int times) {
		
		return (DequeX<T>)MutableCollectionX.super.cycle(m, times);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#cycleWhile(java.util.function.Predicate)
	 */
	@Override
	default DequeX<T> cycleWhile(Predicate<? super T> predicate) {
		
		return (DequeX<T>)MutableCollectionX.super.cycleWhile(predicate);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#cycleUntil(java.util.function.Predicate)
	 */
	@Override
	default DequeX<T> cycleUntil(Predicate<? super T> predicate) {
		
		return (DequeX<T>)MutableCollectionX.super.cycleUntil(predicate);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#zipStream(java.util.stream.Stream)
	 */
	@Override
	default <U> DequeX<Tuple2<T, U>> zipStream(Stream<? extends U> other) {
		
		return (DequeX)MutableCollectionX.super.zipStream(other);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#zip(org.jooq.lambda.Seq)
	 */
	@Override
	default <U> DequeX<Tuple2<T, U>> zip(Seq<? extends U> other) {
		
		return (DequeX)MutableCollectionX.super.zip(other);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#zip3(java.util.stream.Stream, java.util.stream.Stream)
	 */
	@Override
	default <S, U> DequeX<Tuple3<T, S, U>> zip3(Stream<? extends S> second, Stream<? extends U> third) {
		
		return (DequeX)MutableCollectionX.super.zip3(second, third);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#zip4(java.util.stream.Stream, java.util.stream.Stream, java.util.stream.Stream)
	 */
	@Override
	default <T2, T3, T4> DequeX<Tuple4<T, T2, T3, T4>> zip4(Stream<T2> second, Stream<T3> third,
			Stream<T4> fourth) {
		
		return (DequeX<Tuple4<T, T2, T3, T4>>)MutableCollectionX.super.zip4(second, third, fourth);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#zipWithIndex()
	 */
	@Override
	default DequeX<Tuple2<T, Long>> zipWithIndex() {
		//
		return (DequeX<Tuple2<T, Long>>)MutableCollectionX.super.zipWithIndex();
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#distinct()
	 */
	@Override
	default DequeX<T> distinct() {
		
		return (DequeX<T>)MutableCollectionX.super.distinct();
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#sorted()
	 */
	@Override
	default DequeX<T> sorted() {
		
		return (DequeX<T>)MutableCollectionX.super.sorted();
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#sorted(java.util.Comparator)
	 */
	@Override
	default DequeX<T> sorted(Comparator<? super T> c) {
		
		return (DequeX<T>)MutableCollectionX.super.sorted(c);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#skipWhile(java.util.function.Predicate)
	 */
	@Override
	default DequeX<T> skipWhile(Predicate<? super T> p) {
		
		return (DequeX<T>)MutableCollectionX.super.skipWhile(p);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#skipUntil(java.util.function.Predicate)
	 */
	@Override
	default DequeX<T> skipUntil(Predicate<? super T> p) {
		
		return (DequeX<T>)MutableCollectionX.super.skipUntil(p);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#limitWhile(java.util.function.Predicate)
	 */
	@Override
	default DequeX<T> limitWhile(Predicate<? super T> p) {
		
		return (DequeX<T>)MutableCollectionX.super.limitWhile(p);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#limitUntil(java.util.function.Predicate)
	 */
	@Override
	default DequeX<T> limitUntil(Predicate<? super T> p) {
		
		return (DequeX<T>)MutableCollectionX.super.limitUntil(p);
	}

	
	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#intersperse(java.lang.Object)
	 */
	@Override
	default DequeX<T> intersperse(T value) {
		
		return (DequeX<T>)MutableCollectionX.super.intersperse(value);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#shuffle()
	 */
	@Override
	default DequeX<T> shuffle() {
		
		return (DequeX<T>)MutableCollectionX.super.shuffle();
	}

	

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#skipLast(int)
	 */
	@Override
	default DequeX<T> skipLast(int num) {
		
		return (DequeX<T>)MutableCollectionX.super.skipLast(num);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#limitLast(int)
	 */
	@Override
	default DequeX<T> limitLast(int num) {
		
		return (DequeX<T>)MutableCollectionX.super.limitLast(num);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#onEmpty(java.lang.Object)
	 */
	@Override
	default DequeX<T> onEmpty(T value) {
		
		return (DequeX<T>)MutableCollectionX.super.onEmpty(value);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#onEmptyGet(java.util.function.Supplier)
	 */
	@Override
	default DequeX<T> onEmptyGet(Supplier<? extends T> supplier) {
		
		return (DequeX<T>)MutableCollectionX.super.onEmptyGet(supplier);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#onEmptyThrow(java.util.function.Supplier)
	 */
	@Override
	default <X extends Throwable> DequeX<T> onEmptyThrow(Supplier<? extends X> supplier) {
		
		return (DequeX<T>)MutableCollectionX.super.onEmptyThrow(supplier);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#shuffle(java.util.Random)
	 */
	@Override
	default DequeX<T> shuffle(Random random) {
		
		return (DequeX<T>)MutableCollectionX.super.shuffle(random);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#permutations()
	 */
	@Override
	default DequeX<ReactiveSeq<T>> permutations() {
		
		return (DequeX<ReactiveSeq<T>>)MutableCollectionX.super.permutations();
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#combinations(int)
	 */
	@Override
	default DequeX<ReactiveSeq<T>> combinations(int size) {
		
		return (DequeX<ReactiveSeq<T>>)MutableCollectionX.super.combinations(size);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Traversable#combinations()
	 */
	@Override
	default DequeX<ReactiveSeq<T>> combinations() {
		
		return (DequeX<ReactiveSeq<T>>)MutableCollectionX.super.combinations();
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Functor#cast(java.lang.Class)
	 */
	@Override
	default <U> DequeX<U> cast(Class<? extends U> type) {
	
		return (DequeX<U>)MutableCollectionX.super.cast(type);
	}



	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#ofType(java.lang.Class)
	 */
	@Override
	default <U> DequeX<U> ofType(Class<? extends U> type) {
		
		return (DequeX<U>)MutableCollectionX.super.ofType(type);
	}


	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#filterNot(java.util.function.Predicate)
	 */
	@Override
	default DequeX<T> filterNot(Predicate<? super T> fn) {
		
		return  (DequeX<T>)MutableCollectionX.super.filterNot(fn);
	}


	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#notNull()
	 */
	@Override
	default DequeX<T> notNull() {
		
		return  (DequeX<T>)MutableCollectionX.super.notNull();
	}


	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#removeAll(java.util.stream.Stream)
	 */
	@Override
	default DequeX<T> removeAll(Stream<? extends T> stream) {
		
		return  (DequeX<T>)MutableCollectionX.super.removeAll(stream);
	}


	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#removeAll(java.lang.Iterable)
	 */
	@Override
	default DequeX<T> removeAll(Iterable<? extends T> it) {
		
		return  (DequeX<T>)MutableCollectionX.super.removeAll(it);
	}


	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#removeAll(java.lang.Object[])
	 */
	@Override
	default DequeX<T> removeAll(T... values) {
		
		return  (DequeX<T>)MutableCollectionX.super.removeAll(values);
	}


	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#retainAll(java.lang.Iterable)
	 */
	@Override
	default DequeX<T> retainAll(Iterable<? extends T> it) {
		
		return  (DequeX<T>)MutableCollectionX.super.retainAll(it);
	}


	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#retainAll(java.util.stream.Stream)
	 */
	@Override
	default DequeX<T> retainAll(Stream<? extends T> seq) {
		
		return  (DequeX<T>)MutableCollectionX.super.retainAll(seq);
	}


	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#retainAll(java.lang.Object[])
	 */
	@Override
	default DequeX<T> retainAll(T... values) {
		
		return  (DequeX<T>)MutableCollectionX.super.retainAll(values);
	}


    @Override
    default <C extends Collection<? super T>> DequeX<C> grouped(int size, Supplier<C> supplier) {
        
        return (DequeX<C>)MutableCollectionX.super.grouped(size, supplier);
    }


    @Override
    default DequeX<ListX<T>> groupedUntil(Predicate<? super T> predicate) {
        
        return (DequeX<ListX<T>>)MutableCollectionX.super.groupedUntil(predicate);
    }


    @Override
    default DequeX<ListX<T>> groupedWhile(Predicate<? super T> predicate) {
        
        return (DequeX<ListX<T>>)MutableCollectionX.super.groupedWhile(predicate);
    }


    @Override
    default <C extends Collection<? super T>> DequeX<C> groupedWhile(Predicate<? super T> predicate,
            Supplier<C> factory) {
        
        return (DequeX<C>)MutableCollectionX.super.groupedWhile(predicate, factory);
    }


    @Override
    default <C extends Collection<? super T>> DequeX<C> groupedUntil(Predicate<? super T> predicate,
            Supplier<C> factory) {
        
        return (DequeX<C>)MutableCollectionX.super.groupedUntil(predicate, factory);
    }


    @Override
    default DequeX<ListX<T>> groupedStatefullyWhile(BiPredicate<ListX<? super T>, ? super T> predicate) {
        
        return (DequeX<ListX<T>>)MutableCollectionX.super.groupedStatefullyWhile(predicate);
    }
    
    @Override
    default DequeX<T> removeAll(Seq<? extends T> stream) {
       
        return (DequeX<T>)MutableCollectionX.super.removeAll(stream);
    }


    @Override
    default DequeX<T> retainAll(Seq<? extends T> stream) {
       
        return (DequeX<T>)MutableCollectionX.super.retainAll(stream);
    }
    
	
	
}
