package com.aol.cyclops.comprehensions;

//import static fj.data.Option.none;
//import static fj.data.Option.some;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.Test;

import com.aol.cyclops.control.For;

//import fj.data.Option;
import lombok.val;


	
	
public class ForComprehensionTest {
	
	
		@Test
		public void intstream() {
			
			Stream<Integer> res =For.stream(IntStream.range(1,3).boxed()) 
										 .yield( v1-> v1 + 1)
										 .unwrap();
			List<Integer> expected = Arrays.asList(2,3);
			
			
			
			assertThat(expected, equalTo( res.collect(Collectors.toList())));
		}
		@Test
		public void intstream4() {
			
			Stream<Integer> res = For.stream(IntStream.range(0,4)) 
								.yield( v1 -> v1 + 1).unwrap();
			List<Integer> expected = Arrays.asList(1,2,3,4);
			
			
			
			assertThat(expected, equalTo( res.collect(Collectors.toList())));
		}
		@Test
		public void longstream() {
			
			
			
			Stream<Long> res = For.stream(LongStream.range(1l,30l).boxed())
								.stream(a-> LongStream.range(6l,10l))
								.yield(  v1->v2 ->  v1 * v2 + 1l)
								.unwrap();
			
			
			
			List<Integer> expected = Arrays.asList(7, 8, 9, 10, 13, 15, 17, 19, 19, 22, 25, 28, 25, 29, 
										33, 37, 31, 36, 41, 46, 37, 43, 49, 55, 43, 50, 57, 64, 49, 57, 
										65, 73, 55, 64, 73, 82, 61, 71, 81, 91, 67, 78, 89, 100, 73, 85, 
										97, 109, 79, 92, 105, 118, 85, 99, 113, 127, 91, 106, 121, 136, 
										97, 113, 129, 145, 103, 120, 137, 154, 109, 127, 145, 163, 115, 
										134, 153, 172, 121, 141, 161, 181, 127, 148, 169, 190, 133, 155, 
										177, 199, 139, 162, 185, 208, 145, 169, 193, 217, 151, 176, 201, 
										226, 157, 183, 209, 235, 163, 190, 217, 244, 169, 197, 225, 253, 
										175, 204, 233, 262);
			
			
			
			assertThat(expected.stream().map(i->Long.valueOf(i)).collect(Collectors.toList()), equalTo( res.collect(Collectors.toList())));
		}
		@Test
		public void doubleStream() {
			
			
			Stream<Double> res =For.stream( DoubleStream.of(10.00,20.00).boxed())
										.stream(a->DoubleStream.of(2.00,3.50))
										.stream(a->b->DoubleStream.of(25.50))
										.yield( v1->v2->v3-> ( v1 * v2 * v3 )).unwrap();
			
			List<Double> expected = Arrays.asList(510.0, 892.5, 1020.0, 1785.0);
			
			
			
			assertThat(expected, equalTo( res.collect(Collectors.toList())));
		}
	
		@Test
		public void simpleLists() {
		
			
			Stream<Integer> res =For.iterable(Arrays.asList(1,2))
										.yield(v1 -> v1+1)
										.stream();
			
			List<Integer> expected = Arrays.asList(2,3);
		
			assertThat(expected, equalTo( res.collect(Collectors.toList())));
			
		}
		
		
	
}
