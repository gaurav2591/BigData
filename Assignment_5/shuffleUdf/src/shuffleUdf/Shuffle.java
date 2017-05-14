package shuffleUdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;

public class Shuffle extends EvalFunc<Tuple>{

	/**
	 * @return
	 */
	private static StringBuilder shuffleString(String word) {
		List<Character> list = new ArrayList<>();
		for(char c :  word.toCharArray()){
		    list.add(c); 
		}
		Collections.shuffle(list);

		StringBuilder sb = new StringBuilder();
		for(char character : list)
		  sb.append(character);
		return sb;
	}

	@Override
	public Tuple exec(Tuple tuple) throws IOException {
		if(tuple == null || tuple.size() == 0){
			return null;
		}
		String[] array = tuple.get(0).toString().split("@");
		String part1 = shuffleString(array[0]).toString();
		return TupleFactory.getInstance().newTuple((part1+"@"+array[1]));
	}


}
