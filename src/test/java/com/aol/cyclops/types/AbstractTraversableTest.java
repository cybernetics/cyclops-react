package com.aol.cyclops.types;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.jooq.lambda.tuple.Tuple.tuple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.junit.Before;
import org.junit.Test;

import com.aol.cyclops.CyclopsCollectors;
import com.aol.cyclops.Monoid;
import com.aol.cyclops.Reducers;
import com.aol.cyclops.Semigroups;
import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.data.collections.extensions.standard.ListXImpl;
import com.aol.cyclops.types.stream.reactive.SeqSubscriber;

public abstract class AbstractTraversableTest {
    public abstract <T> Traversable<T> of(T...elements);
    public abstract <T> Traversable<T> empty();
    
    @Test
    public void publishAndSubscribe(){
        
        SeqSubscriber<Integer> sub = ReactiveSeq.subscriber();
    
        of(1,2,3).subscribe(sub);
        
        assertThat(sub.stream().toList(),hasItems(
                1,2,3));
        
    }
    @Test
    public void stream(){
       
        assertThat(of(1,2,3).stream().collect(Collectors.toList()),hasItems(1,2,3));
    }

       @Test
    public void dropRight(){
        assertThat(of(1,2,3).dropRight(1).stream().toList(),hasItems(1,2));
    }
    @Test
    public void dropRightEmpty(){
        assertThat(of().dropRight(1).stream().toList(),equalTo(Arrays.asList()));
    }
    
    @Test
    public void dropUntil(){
        assertThat(of(1,2,3,4,5).dropUntil(p->p==2).stream().toList().size(),lessThan(5));
    }
    @Test
    public void dropUntilEmpty(){
        assertThat(of().dropUntil(p->true).stream().toList(),equalTo(Arrays.asList()));
    }
    @Test
    public void dropWhile(){
        assertThat(of(1,2,3,4,5).dropWhile(p->p<6).stream().toList().size(),lessThan(1));
    }
    @Test
    public void dropWhileEmpty(){
        assertThat(of().dropWhile(p->true).stream().toList(),equalTo(Arrays.asList()));
    }
   
    
    
    
    
    Traversable<Integer> empty;
    Traversable<Integer> nonEmpty;

    @Before
    public void setup(){
        empty = of();
        nonEmpty = of(1);
    }

    
    protected Object value() {
        
        return "jello";
    }
    private int value2() {
        
        return 200;
    }
    
    
    @Test
    public void batchBySize(){
        System.out.println(of(1,2,3,4,5,6).grouped(3).stream().collect(Collectors.toList()));
        assertThat(of(1,2,3,4,5,6).grouped(3).stream().collect(Collectors.toList()).size(),is(2));
    }
    

    

    
    @Test
    public void limitWhileTest(){
        
        List<Integer> list = new ArrayList<>();
        while(list.size()==0){
            list = of(1,2,3,4,5,6).takeWhile(it -> it<4)
                    .stream().peek(it -> System.out.println(it)).collect(Collectors.toList());
    
        }
        assertThat(Arrays.asList(1,2,3,4,5,6),hasItem(list.get(0)));
        
        
        
        
    }

    @Test
    public void testScanLeftStringConcat() {
        assertThat(of("a", "b", "c").scanLeft("", String::concat).stream().toList().size(),
                is(4));
    }
   
    @Test
    public void testScanRightStringConcatMonoid() {
        assertThat(of("a", "b", "c").scanRight(Monoid.of("", String::concat)).stream().toList().size(),
            is(asList("", "c", "bc", "abc").size()));
    }
    @Test
    public void testScanRightStringConcat() {
        assertThat(of("a", "b", "c").scanRight("", String::concat).stream().toList().size(),
            is(asList("", "c", "bc", "abc").size()));
    }
  

    

   
    

   
   
    
    @Test
    public void testIterable() {
        List<Integer> list = of(1, 2, 3).stream().toCollection(LinkedList::new);

        for (Integer i :of(1, 2, 3)) {
            assertThat(list,hasItem(i));
        }
    }
    

       
        @Test
        public void testSkipWhile() {
            Supplier<Traversable<Integer>> s = () -> of(1, 2, 3, 4, 5);

            assertTrue(s.get().dropWhile(i -> false).stream().toList().containsAll(asList(1, 2, 3, 4, 5)));
          
            assertEquals(asList(), s.get().dropWhile(i -> true).stream().toList());
        }

        @Test
        public void testSkipUntil() {
            Supplier<Traversable<Integer>> s = () -> of(1, 2, 3, 4, 5);

            assertEquals(asList(), s.get().dropUntil(i -> false).stream().toList());
            assertTrue(s.get().dropUntil(i -> true).stream().toList().containsAll(asList(1, 2, 3, 4, 5)));
          }

       

        @Test
        public void testLimitWhile() {
            Supplier<Traversable<Integer>> s = () -> of(1, 2, 3, 4, 5);

            assertEquals(asList(), s.get().takeWhile(i -> false).stream().toList());
            assertTrue( s.get().takeWhile(i -> i < 3).stream().toList().size()!=5);       
            assertTrue(s.get().takeWhile(i -> true).stream().toList().containsAll(asList(1, 2, 3, 4, 5)));
        }

        @Test
        public void testLimitUntil() {
            

            assertTrue(of(1, 2, 3, 4, 5).takeUntil(i -> false).stream().toList().containsAll(asList(1, 2, 3, 4, 5)));
            assertFalse(of(1, 2, 3, 4, 5).takeUntil(i -> i % 3 == 0).stream().toList().size()==5);
            
            assertEquals(asList(), of(1, 2, 3, 4, 5).takeUntil(i -> true).stream().toList());
        }

       

        
        @Test
        public void zip(){
            List<Tuple2<Integer,Integer>> list =
                    of(1,2,3,4,5,6).zip(of(100,200,300,400).stream()).stream()
                                                    .peek(it -> System.out.println(it))
                                                    .collect(Collectors.toList());
            System.out.println("list = " +list);
            
            List<Integer> right = list.stream().map(t -> t.v2).collect(Collectors.toList());
            
            assertThat(right,hasItem(100));
            assertThat(right,hasItem(200));
            assertThat(right,hasItem(300));
            assertThat(right,hasItem(400));
            
            List<Integer> left = list.stream().map(t -> t.v1).collect(Collectors.toList());
            assertThat(Arrays.asList(1,2,3,4,5,6),hasItem(left.get(0)));
            
            
        }

        
        @Test
        public void testScanLeftStringConcatMonoid() {
            assertThat(of("a", "b", "c").scanLeft(Reducers.toString("")).stream().toList(), is(asList("", "a", "ab", "abc")));
        }

       

        

        
  
  
  
 
   
    
    @Test
    public void limitTimeEmpty(){
        List<Integer> result = ReactiveSeq.<Integer>of()
                                        .peek(i->sleep(i*100))
                                        .limit(1000,TimeUnit.MILLISECONDS)
                                        .toList();
        
        
        assertThat(result,equalTo(Arrays.asList()));
    }
    
    @Test
    public void skipTimeEmpty(){
        List<Integer> result = ReactiveSeq.<Integer>of()
                                        .peek(i->sleep(i*100))
                                        .skip(1000,TimeUnit.MILLISECONDS)
                                        .toList();
        
        
        assertThat(result,equalTo(Arrays.asList()));
    }
    private int sleep(Integer i) {
        try {
            Thread.currentThread().sleep(i);
        } catch (InterruptedException e) {
            
        }
        return i;
    }
    @Test
    public void testSkipLast(){
        assertThat(of(1,2,3,4,5)
                            .skipLast(2)
                            .stream()
                            .toListX(),equalTo(Arrays.asList(1,2,3)));
    }
    @Test
    public void testSkipLastEmpty(){
        assertThat(of()
                            .skipLast(2)
                            .stream().collect(Collectors.toList()),equalTo(Arrays.asList()));
    }
    @Test
    public void testLimitLast(){
        assertThat(of(1,2,3,4,5)
                            .limitLast(2)
                            .stream().collect(Collectors.toList()),equalTo(Arrays.asList(4,5)));
    }
    @Test
    public void testLimitLastEmpty(){
        assertThat(of()
                            .limitLast(2)
                            .stream().collect(Collectors.toList()),equalTo(Arrays.asList()));
    }
  
  
    
    @Test
    public void zip2of(){
        
        List<Tuple2<Integer,Integer>> list =of(1,2,3,4,5,6)
                                            .zip(of(100,200,300,400).stream())
                                            .stream().toListX();
                
    
        List<Integer> right = list.stream().map(t -> t.v2).collect(Collectors.toList());
        assertThat(right,hasItem(100));
        assertThat(right,hasItem(200));
        assertThat(right,hasItem(300));
        assertThat(right,hasItem(400));
        
        List<Integer> left = list.stream().map(t -> t.v1).collect(Collectors.toList());
        assertThat(Arrays.asList(1,2,3,4,5,6),hasItem(left.get(0)));

    }
    @Test
    public void zipInOrder(){
        
        List<Tuple2<Integer,Integer>> list =  of(1,2,3,4,5,6)
                                                    .zip( of(100,200,300,400).stream())
                                                    .stream().toListX();
        
        assertThat(asList(1,2,3,4,5,6),hasItem(list.get(0).v1));
        assertThat(asList(100,200,300,400),hasItem(list.get(0).v2));
        
        
        
    }

    @Test
    public void zipEmpty() throws Exception {
        
        
        final Traversable<Integer> zipped = this.<Integer>empty().zip(ReactiveSeq.<Integer>of(), (a, b) -> a + b);
        assertTrue(zipped.stream().collect(Collectors.toList()).isEmpty());
    }

    @Test
    public void shouldReturnEmptySeqWhenZipEmptyWithNonEmpty() throws Exception {
        
        
        
        final Traversable<Integer> zipped = this.<Integer>empty().zip(of(1,2), (a, b) -> a + b);
        assertTrue(zipped.stream().collect(Collectors.toList()).isEmpty());
    }

    @Test
    public void shouldReturnEmptySeqWhenZipNonEmptyWithEmpty() throws Exception {
        
        
        final Traversable<Integer> zipped = of(1,2,3).zip(this.<Integer>empty(), (a, b) -> a + b);

        
        assertTrue(zipped.stream().collect(Collectors.toList()).isEmpty());
    }

    @Test
    public void shouldZipTwoFiniteSequencesOfSameSize() throws Exception {
        
        final Traversable<String> first = of("A", "B", "C");
        final Traversable<Integer> second = of(1, 2, 3);

        
        final Traversable<String> zipped = first.zip(second, (a, b) -> a + b);

        
        assertThat(zipped.stream().collect(Collectors.toList()).size(),is(3));
    }

    

    @Test
    public void shouldTrimSecondFixedSeqIfLonger() throws Exception {
        final Traversable<String> first = of("A", "B", "C");
        final Traversable<Integer> second = of(1, 2, 3, 4);

        
        final Traversable<String> zipped = first.zip(second, (a, b) -> a + b);

        assertThat(zipped.stream().collect(Collectors.toList()).size(),is(3));
    }

    @Test
    public void shouldTrimFirstFixedSeqIfLonger() throws Exception {
        final Traversable<String> first = of("A", "B", "C","D");
        final Traversable<Integer> second = of(1, 2, 3);
        final Traversable<String> zipped = first.zip(second, (a, b) -> a + b);

        
        assertThat(zipped.stream().collect(Collectors.toList()).size(),equalTo(3));
    }

    @Test
    public void testZipDifferingLength() {
        List<Tuple2<Integer, String>> list = of(1, 2).zip(of("a", "b", "c", "d").stream()).stream().toList();

        assertEquals(2, list.size());
        assertTrue(asList(1, 2).contains(list.get(0).v1));
        assertTrue("" + list.get(1).v2, asList(1, 2).contains(list.get(1).v1));
        assertTrue(asList("a", "b", "c", "d").contains(list.get(0).v2));
        assertTrue(asList("a", "b", "c", "d").contains(list.get(1).v2));

    }

    
    @Test
    public void shouldTrimSecondFixedSeqIfLongerStream() throws Exception {
        final Traversable<String> first = of("A", "B", "C");
        final Traversable<Integer> second = of(1, 2, 3, 4);

        
        final Traversable<String> zipped = first.zip(second, (a, b) -> a + b);

        assertThat(zipped.stream().collect(Collectors.toList()).size(),is(3));
    }

    @Test
    public void shouldTrimFirstFixedSeqIfLongerStream() throws Exception {
        final Traversable<String> first = of("A", "B", "C","D");
        final Traversable<Integer> second = of(1, 2, 3);
        
        final Traversable<String> zipped = first.zip(second, (a, b) -> a + b);

        
        assertThat(zipped.stream().collect(Collectors.toList()).size(),equalTo(3));
    }

    @Test
    public void testZipDifferingLengthStream() {
        List<Tuple2<Integer, String>> list = of(1, 2).zip(of("a", "b", "c", "d").stream()).stream().toList();

        assertEquals(2, list.size());
        assertTrue(asList(1, 2).contains(list.get(0).v1));
        assertTrue("" + list.get(1).v2, asList(1, 2).contains(list.get(1).v1));
        assertTrue(asList("a", "b", "c", "d").contains(list.get(0).v2));
        assertTrue(asList("a", "b", "c", "d").contains(list.get(1).v2));

    }

    @Test
    public void shouldTrimSecondFixedSeqIfLongerSequence() throws Exception {
        final Traversable<String> first = of("A", "B", "C");
        final Traversable<Integer> second = of(1, 2, 3, 4);

        
        final Traversable<String> zipped = first.zip(second, (a, b) -> a + b);

        assertThat(zipped.stream().collect(Collectors.toList()).size(),is(3));
    }

    @Test
    public void shouldTrimFirstFixedSeqIfLongerSequence() throws Exception {
        final Traversable<String> first = of("A", "B", "C","D");
        final Traversable<Integer> second = of(1, 2, 3);
        final Traversable<String> zipped = first.zip(second, (a, b) -> a + b);

        
        assertThat(zipped.stream().collect(Collectors.toList()).size(),equalTo(3));
    }

    
    @Test
    public void testZipWithIndex() {
        assertEquals(asList(), of().zipWithIndex().stream().toListX());

        assertThat(of("a").zipWithIndex().stream().map(t -> t.v2).findFirst().get(), is(0l));
        assertEquals(asList(new Tuple2("a", 0L)), of("a").zipWithIndex().stream().toListX());

    }
    
    
    
    
  

     
        
        @Test
        public void batchBySizeCollection(){
            
            
            assertThat(of(1,2,3,4,5,6).grouped(3,()->new ListXImpl<Integer>()).stream().get(0).get().size(),is(3));
            
           // assertThat(of(1,1,1,1,1,1).grouped(3,()->new ListXImpl<>()).get(1).get().size(),is(1));
        }
        @Test
        public void batchBySizeInternalSize(){
            assertThat(of(1,2,3,4,5,6).grouped(3).stream().collect(Collectors.toList()).get(0).size(),is(3));
        }
        
       
       
        
        @Test
        public void testSorted() {
         
            AnyM.fromList(ListX.of(tuple(2, 2), tuple(1, 1))).printOut();
            Traversable<Tuple2<Integer, Integer>> t1 = of(tuple(2, 2), tuple(1, 1));
           
            List<Tuple2<Integer, Integer>> s1 = t1.sorted().stream().toList();
            assertEquals(tuple(1, 1), s1.get(0));
            assertEquals(tuple(2, 2), s1.get(1));

            Traversable<Tuple2<Integer, String>> t2 = of(tuple(2, "two"), tuple(1, "one"));
            List<Tuple2<Integer, String>> s2 = t2.sorted(comparing(t -> t.v1())).stream().toList();
            assertEquals(tuple(1, "one"), s2.get(0));
            assertEquals(tuple(2, "two"), s2.get(1));

            Traversable<Tuple2<Integer, String>> t3 = of(tuple(2, "two"), tuple(1, "one"));
            List<Tuple2<Integer, String>> s3 = t3.sorted(t -> t.v1()).stream().toList();
            assertEquals(tuple(1, "one"), s3.get(0));
            assertEquals(tuple(2, "two"), s3.get(1));
        }

        @Test
        public void zip2(){
            List<Tuple2<Integer,Integer>> list =
                    of(1,2,3,4,5,6).zipStream(Stream.of(100,200,300,400))
                                                    .stream()
                                                    .peek(it -> System.out.println(it))
                                                    .collect(Collectors.toList());
            
            List<Integer> right = list.stream().map(t -> t.v2).collect(Collectors.toList());
            assertThat(right,hasItem(100));
            assertThat(right,hasItem(200));
            assertThat(right,hasItem(300));
            assertThat(right,hasItem(400));
            
            List<Integer> left = list.stream().map(t -> t.v1).collect(Collectors.toList());
            assertThat(Arrays.asList(1,2,3,4,5,6),hasItem(left.get(0)));
            
            
        }
        
        

        @Test
        public void testReverse() {
            assertThat( of(1, 2, 3).reverse().stream().toList().size(), is(asList(3, 2, 1).size()));
        }

        @Test
        public void testShuffle() {
            Supplier<Traversable<Integer>> s = () ->of(1, 2, 3);

            assertEquals(3, s.get().shuffle().stream().toListX().size());
            assertThat(s.get().shuffle().stream().toListX(), hasItems(1, 2, 3));

            
        }
        @Test
        public void testShuffleRandom() {
            Random r = new Random();
            Supplier<Traversable<Integer>> s = () ->of(1, 2, 3);

            assertEquals(3, s.get().shuffle(r).stream().toListX().size());
            assertThat(s.get().shuffle(r).stream().toListX(), hasItems(1, 2, 3));

            
        }

        
        
        
        
       
        
        

            
            @Test
            public void batchUntil(){
             
                assertThat(of(1,2,3,4,5,6)
                        .groupedUntil(i->false).stream()
                        .toListX().get(0).size(),equalTo(6));
               
            }
            @Test
            public void batchWhile(){
               
               assertThat(of(1,2,3,4,5,6).stream().peek(System.out::println)
                        .groupedWhile(i->true)
                        .toListX().get(0)
                        .size(),equalTo(6));
               
            }
            @Test
            public void batchUntilSupplier(){
                assertThat(of(1,2,3,4,5,6)
                        .groupedUntil(i->false,()->new ListXImpl()).stream()
                        .toListX().size(),equalTo(1));
               
            }
            @Test
            public void batchWhileSupplier(){
                assertThat(of(1,2,3,4,5,6)
                        .groupedWhile(i->true,()->new ListXImpl()).stream()
                        .toListX()
                        .size(),equalTo(1));
               
            }
          
            @Test
            public void slidingNoOrder() {
                ListX<ListX<Integer>> list = of(1, 2, 3, 4, 5, 6).sliding(2).stream().toListX();

                System.out.println(list);
                assertThat(list.get(0).size(), equalTo(2));
                assertThat(list.get(1).size(), equalTo(2));
            }

            @Test
            public void slidingIncrementNoOrder() {
                List<List<Integer>> list = of(1, 2, 3, 4, 5, 6).sliding(3, 2).stream().collect(Collectors.toList());

                System.out.println(list);
               
                assertThat(list.get(1).size(), greaterThan(1));
            }

            @Test
            public void combineNoOrder(){
                assertThat(of(1,2,3)
                           .combine((a, b)->a.equals(b),Semigroups.intSum).stream()
                           .toListX(),equalTo(ListX.of(1,2,3))); 
                           
            }
            @Test
            public void groupedFunctionNoOrder(){
                assertThat(of(1,2,3).grouped(f-> f<3? "a" : "b").stream().count(),equalTo((2L)));
                assertThat(of(1,2,3).grouped(f-> f<3? "a" : "b").stream().filter(t->t.v1.equals("a"))
                                .map(t->t.v2).map(ReactiveSeq::fromStream).map(ReactiveSeq::toListX).single(),
                                    equalTo((ListX.of(1,2))));
            }
            @Test
            public void groupedFunctionCollectorNoOrder(){
                assertThat(of(1,2,3).grouped(f-> f<3? "a" : "b",CyclopsCollectors.toListX()).stream().count(),equalTo((2L)));
                assertThat(of(1,2,3).grouped(f-> f<3? "a" : "b",CyclopsCollectors.toListX()).stream().filter(t->t.v1.equals("a"))
                        .map(t->t.v2).single(),
                            equalTo((Arrays.asList(1,2))));
            }
            @Test
            public void zip3NoOrder(){
                List<Tuple3<Integer,Integer,Character>> list =
                        of(1,2,3,4).zip3(of(100,200,300,400).stream(),of('a','b','c','d').stream()).stream()
                                                        .toListX();
                
                System.out.println(list);
                List<Integer> right = list.stream().map(t -> t.v2).collect(Collectors.toList());
                assertThat(right,hasItem(100));
                assertThat(right,hasItem(200));
                assertThat(right,hasItem(300));
                assertThat(right,hasItem(400));
                
                List<Integer> left = list.stream().map(t -> t.v1).collect(Collectors.toList());
                assertThat(Arrays.asList(1,2,3,4),hasItem(left.get(0)));
                
                List<Character> three = list.stream().map(t -> t.v3).collect(Collectors.toList());
                assertThat(Arrays.asList('a','b','c','d'),hasItem(three.get(0)));
                
                
            }
            @Test
            public void zip4NoOrder(){
                List<Tuple4<Integer,Integer,Character,String>> list =
                        of(1,2,3,4).zip4(of(100,200,300,400).stream(),of('a','b','c','d').stream(),of("hello","world","boo!","2").stream()).stream()
                                                        .toListX();
                System.out.println(list);
                List<Integer> right = list.stream().map(t -> t.v2).collect(Collectors.toList());
                assertThat(right,hasItem(100));
                assertThat(right,hasItem(200));
                assertThat(right,hasItem(300));
                assertThat(right,hasItem(400));
                
                List<Integer> left = list.stream().map(t -> t.v1).collect(Collectors.toList());
                assertThat(Arrays.asList(1,2,3,4),hasItem(left.get(0)));
                
                List<Character> three = list.stream().map(t -> t.v3).collect(Collectors.toList());
                assertThat(Arrays.asList('a','b','c','d'),hasItem(three.get(0)));
            
                List<String> four = list.stream().map(t -> t.v4).collect(Collectors.toList());
                assertThat(Arrays.asList("hello","world","boo!","2"),hasItem(four.get(0)));
                
                
            }
            
            @Test
            public void testIntersperseNoOrder() {
                
                assertThat((of(1,2,3).intersperse(0)).stream().toListX(),hasItem(0));
            



            }
         
          
}
