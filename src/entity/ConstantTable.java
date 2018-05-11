package entity;

import java.util.*;

/*
 * 字符串常量表,用于后面的中间代码生成
 */
public class ConstantTable implements Iterable<ConstantEntry>{
	Map<String, ConstantEntry>table;
	 public ConstantTable(){
		table = new LinkedHashMap<String, ConstantEntry>();
	 }
	 public boolean isEmpty() {
	        return table.isEmpty();
	    }

	    public ConstantEntry insert(String s) {
	        ConstantEntry ent = table.get(s);
	        if (ent == null) {
	            ent = new ConstantEntry(s);
	            table.put(s, ent);
	        }
	        return ent;
	    }

	    public Collection<ConstantEntry> entries() {
	        return table.values();
	    }
	public Iterator<ConstantEntry> iterator() {
		return table.values().iterator();
	}
}
