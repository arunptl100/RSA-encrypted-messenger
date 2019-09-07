import java.io.*;

public class clientpkey implements Serializable{
  private static final long serialVersionUID = 1L;
  int n;
  int e;
  int id;
  String username;
  public clientpkey(int n, int e){
    this.n = n;
    this.e = e;
  }
}
