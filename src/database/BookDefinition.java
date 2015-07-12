
package database;

/**
 * Structure which defines a book. It contains the author and the title.
 */
public class BookDefinition {

	private String _title;
	private String _author;
	private long _id;

	public BookDefinition(String title, String author) {
		this.initialize(title, author, -1);
	}

	public BookDefinition(String title, String author, long id) {
		this.initialize(title, author, id);
	}

	private void initialize(String title, String author, long id) {
		_title = title;
		_author = author;
		_id = id;
	}
	
	public String getTitle(){
		return _title;
	}
	
	public String getAuthor(){
		return _author;
	}
	
	public long getId(){
		return _id;
	}
	
	public void setTitle(String title){
		_title = title;
	}
	
	public void setAuthor(String author){
		_author = author;
	}
	
	public void setId(long id){
		_id = id;
	}
}