package com.aol.cyclops.types.stream;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import com.aol.cyclops.internal.stream.BaseHotStreamImpl;

public class NonPausableHotStream<T>  extends BaseHotStreamImpl<T> {
	public NonPausableHotStream(Stream<T> stream) {
		super(stream);
	}

	public HotStream<T> init(Executor exec){
		CompletableFuture.runAsync( ()-> {
			pause.get().join();
			stream.forEach(a->{
					
					int local = connected;
					
					for(int i=0;i<local;i++){
					
						connections.get(i).offer(a);
					}
					
					
				});
				
				open.set(false); 
				
					
		},exec);
		return this;
	}
}
