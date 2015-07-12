
package globals;

import async.ProgressiveAction;

public class Globals {

	public ProgressiveAction   databaseAction;
	public String              listWhereClause;
	public String              listOrderByClause;
	public String              fragFilterText;
	public int                 fragFilterId;
	public int                 fragSortId;
	
	private static Globals _instance;
	
	private Globals(){
	}

	public static Globals getInstance(){
		
		if(null == _instance)
			_instance = new Globals();
		
		return _instance;
	}
}
