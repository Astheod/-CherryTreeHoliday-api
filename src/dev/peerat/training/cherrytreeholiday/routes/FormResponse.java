package dev.peerat.training.cherrytreeholiday.routes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import dev.peerat.framework.Context;
import dev.peerat.framework.HttpReader;
import dev.peerat.framework.HttpWriter;
import dev.peerat.framework.Response;
import dev.peerat.framework.utils.json.Json;

public abstract class FormResponse implements Response{

	private Json json;
	private Set<Function<Json, Boolean>> required;
	private Map<Function<Json, Json>, Function<Json, Boolean>> checkers;
	
	public FormResponse(){
		this.required = new HashSet<>();
		this.checkers = new HashMap<>();
	}
	
	public void require(Function<Json, Boolean> func){
		this.required.add(func);
	}
	
	public void hasLength(Function<Json, Json> path, Function<Json, String> getter, int min, int max){
		this.checkers.put(path, (json) -> {
			int length = getter.apply(json).length();
			return length >= min && length <= max;
		});
	}
	
	public <J extends Json> J readJson(HttpReader reader) throws Exception{
		return (J) (this.json = reader.readJson());
	}
	
	public boolean isValid(Executable onError) throws Exception{
		for(Function<Json, Boolean> func : required){
			if(!func.apply(json)){
				onError.run();
				return false;
			}
		}
		for(Entry<Function<Json, Json>, Function<Json, Boolean>> entry : checkers.entrySet()){
			Json target = entry.getKey().apply(json);
			if(!entry.getValue().apply(target)){
				onError.run();
				return false;
			}
		}
		return true;
	}
	
	public static interface Executable{
		
		void run() throws Exception;
		
	}
}
