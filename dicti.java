import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
 
public class dicti{	//dicti	
	static tlli tl = new tlli();
	static int key_root,value_root,level_root;
	
    public static void main( String[] args ) {	//dictionary program
		int point = 0;	//total point
		key_root = tl.allocate_triany();	//make a element of root
		value_root = tl.allocate_triany();
		level_root = tl.allocate_triany();
 
		try{
			FileReader f = new FileReader("testdata.txt");
			BufferedReader b = new BufferedReader(f);
			String s;
			Pattern p = Pattern.compile("^#");
			while((s = b.readLine())!=null){							
				if(p.matcher(s).find() == true){	//if first character is '#'.
					continue;
				}			
				String[] _s = s.split(" ");
				if(_s[0].equals("s")){	//if first character is s
					set_entry(Integer.parseInt(_s[1]),Integer.parseInt(_s[2]));
				}
				else if(_s[0].equals("f")){	//if first character is s
					point += find_entry(Integer.parseInt(_s[1]));
				}	
			}
		}catch(Exception e){
			System.out.println("error");
		}		
		System.out.println(point);	//output the total point
	}
	
	static void set_entry(int key,int value){
		if(key < 1 || value < 1) return;	//exception	
		int key_id = key_root;
		int value_id = value_root;
		int level_id = level_root;
		int axis = -1;
		int dir = -1;	//left:0, right:1
		ArrayList<Integer> pre_key = new ArrayList<Integer>(); //save previous key id
		ArrayList<Integer> pre_value = new ArrayList<Integer>(); //save previous value id
		ArrayList<Integer> pre_level = new ArrayList<Integer>(); //save previous level id
		
		if(tl.get_a(key_id) == 0){	//initialize
			tl.set_a(key_id,key);
			tl.set_a(value_id,value);
			tl.set_a(level_id,1);
			return;
		}
 
		//search the key by binary search tree
		int node_key = tl.get_a(key_id);
		while(node_key != key){
			pre_key.add(key_id);
			pre_value.add(value_id);
			pre_level.add(level_id);
			axis++;
			
			if(key < node_key){
				key_id = tl.get_b(key_id);
				value_id = tl.get_b(value_id);
				level_id = tl.get_b(level_id);
			}
			else{
				key_id = tl.get_c(key_id);
				value_id = tl.get_c(value_id);
				level_id = tl.get_c(level_id);
			}
			if(key_id == 0) break;
			node_key = tl.get_a(key_id);
		}
 
		if(key_id == 0){ //if the key does not exist, create a new element in the tree
			int new_key_id = tl.allocate_triany();
			int new_value_id = tl.allocate_triany();
			int new_level_id = tl.allocate_triany();
			tl.set_a(new_key_id,key);
			tl.set_a(new_value_id,value);
			tl.set_a(new_level_id,1);
			if(key < tl.get_a(pre_key.get(axis))){
				dir = 0;
				tl.set_b(pre_key.get(axis),new_key_id);
				tl.set_b(pre_value.get(axis),new_value_id);
				tl.set_b(pre_level.get(axis),new_level_id);
			}
			else{
				dir = 1;
				tl.set_c(pre_key.get(axis),new_key_id);
				tl.set_c(pre_value.get(axis),new_value_id);
				tl.set_c(pre_level.get(axis),new_level_id);
			}
 
			//tree rotation
			while(true){
				if(dir == 0){
					int seed_level = tl.get_a(tl.get_b(pre_level.get(axis)));
					if(axis < 0 || tl.get_a(pre_level.get(axis)) > seed_level) break;
					//skew
					if(axis > 0){
						if( tl.get_a(pre_key.get(axis)) >  tl.get_a(pre_key.get(axis-1)) ){
							tl.set_c(pre_key.get(axis-1),rotate(0,pre_key.get(axis)));
							tl.set_c(pre_value.get(axis-1),rotate(0,pre_value.get(axis)));
							tl.set_c(pre_level.get(axis-1),rotate(0,pre_level.get(axis)));
						}
						else{
							tl.set_b(pre_key.get(axis-1),rotate(0,pre_key.get(axis)));
							tl.set_b(pre_value.get(axis-1),rotate(0,pre_value.get(axis)));
							tl.set_b(pre_level.get(axis-1),rotate(0,pre_level.get(axis)));
						}
					}
					else{
						key_root = rotate(0,pre_key.get(axis));
						value_root = rotate(0,pre_value.get(axis));
						level_root = rotate(0,pre_level.get(axis));
						break;
					}
	
					if(tl.get_a(tl.get_c(pre_level.get(axis))) == seed_level){
						//split
						if( tl.get_a(pre_key.get(axis)) >  tl.get_a(pre_key.get(axis-1)) ){
							tl.set_c(pre_key.get(axis-1),rotate(1,tl.get_c(pre_key.get(axis-1))));
							tl.set_c(pre_value.get(axis-1),rotate(1,tl.get_c(pre_value.get(axis-1))));
							tl.set_c(pre_level.get(axis-1),rotate(1,tl.get_c(pre_level.get(axis-1))));
							dir = 1;
						}
						else{
							tl.set_b(pre_key.get(axis-1),rotate(1,tl.get_b(pre_key.get(axis-1))));
							tl.set_b(pre_value.get(axis-1),rotate(1,tl.get_b(pre_value.get(axis-1))));
							tl.set_b(pre_level.get(axis-1),rotate(1,tl.get_b(pre_level.get(axis-1))));
						}
						tl.set_a(pre_level.get(axis),tl.get_a(pre_level.get(axis))+1);
						axis--;
					}
					else if(axis > 0 && tl.get_a(pre_level.get(axis-1)) == seed_level){
						//split
						if(axis == 1){
							key_root = rotate(1,key_root);
							value_root = rotate(1,value_root);
							level_root = rotate(1,level_root);
							tl.set_a(level_root,tl.get_a(level_root)+1);
							break;
						}
						else if( tl.get_a(pre_key.get(axis-1)) >  tl.get_a(pre_key.get(axis-2)) ){
							tl.set_c(pre_key.get(axis-2),rotate(1,pre_key.get(axis-1)));
							tl.set_c(pre_value.get(axis-2),rotate(1,pre_value.get(axis-1)));
							tl.set_c(pre_level.get(axis-2),rotate(1,pre_level.get(axis-1)));
							tl.set_a(tl.get_c(pre_level.get(axis-2)),tl.get_a(tl.get_c(pre_level.get(axis-2)))+1);
							dir = 1;
						}
						else{
							tl.set_b(pre_key.get(axis-2),rotate(1,pre_key.get(axis-1)));
							tl.set_b(pre_value.get(axis-2),rotate(1,pre_value.get(axis-1)));
							tl.set_b(pre_level.get(axis-2),rotate(1,pre_level.get(axis-1)));
							tl.set_a(tl.get_b(pre_level.get(axis-2)),tl.get_a(tl.get_b(pre_level.get(axis-2)))+1);
						}
						axis -= 2;
					}
					else break;
				}
				else if(dir == 1){
					if(axis < 1) break;
					if(tl.get_a(pre_level.get(axis-1)) > tl.get_a(tl.get_c(pre_level.get(axis))) ) break;
					//split
					if(axis == 1){
						key_root = rotate(1,key_root);
						value_root = rotate(1,value_root);
						level_root = rotate(1,level_root);
						tl.set_a(level_root,tl.get_a(level_root)+1);
						break;
					}
					else if(tl.get_a(pre_key.get(axis-1)) > tl.get_a(pre_key.get(axis-2))){
						tl.set_c(pre_key.get(axis-2),rotate(1,pre_key.get(axis-1)));
						tl.set_c(pre_value.get(axis-2),rotate(1,pre_value.get(axis-1)));
						tl.set_c(pre_level.get(axis-2),rotate(1,pre_level.get(axis-1)));
						tl.set_a(pre_level.get(axis),tl.get_a(pre_level.get(axis))+1);
					}
					else{
						tl.set_b(pre_key.get(axis-2),rotate(1,pre_key.get(axis-1)));
						tl.set_b(pre_value.get(axis-2),rotate(1,pre_value.get(axis-1)));
						tl.set_b(pre_level.get(axis-2),rotate(1,pre_level.get(axis-1)));
						tl.set_a(pre_level.get(axis),tl.get_a(pre_level.get(axis))+1);
						dir = 0;
					}
					axis -= 2;
				}
			}					
		}
		else{	//if the key already exist
			tl.set_a(value_id,value);
		}
	}
	
	static int find_entry(int key){
		int key_id = key_root;
		int value_id = value_root;
		//search the key
		int node_key = tl.get_a(key_id);
		while(node_key != key){
			if(node_key > key){
				key_id = tl.get_b(key_id);
				value_id = tl.get_b(value_id);
			}
			else{
				key_id = tl.get_c(key_id);
				value_id = tl.get_c(value_id);
			}
			if(key_id == 0) break;
			node_key = tl.get_a(key_id);
		}
		
		if(key_id == 0){
			return 0;	//if the key does not exist
		}
		else{	//if the key exist
			return tl.get_a(value_id);
		}
	}
	
	static int rotate(int dir,int root){
		int pivot = 0;
		if(dir == 0){
			pivot = tl.get_b(root);
			tl.set_b(root,tl.get_c(pivot));
			tl.set_c(pivot,root);
		}
		else if(dir == 1){
			pivot = tl.get_c(root);
			tl.set_c(root,tl.get_b(pivot));
			tl.set_b(pivot,root);
		}
		return pivot;
	}
}
 
class tlli{	//tlli		
	protected ArrayList<String> ids = new ArrayList<String>();	//id set
 	protected ArrayList<Integer> a = new ArrayList<Integer>();
	protected ArrayList<Integer> b = new ArrayList<Integer>();
	protected ArrayList<Integer> c = new ArrayList<Integer>();
	
	int root_triany(){
		int id_n = ids.indexOf(Integer.toString(1));
		if(id_n == -1){	//initialization
			ids.add("1");
			a.add(0);
			b.add(0);
			c.add(0);
		}
		else{
			a.set(0,0);
			b.set(0,0);
			c.set(0,0);	
		}
		return 1;
	}
	
	int allocate_triany(){
		Random rnd = new Random();	
		int ran = rnd.nextInt(1000000000)+2;	//0 or 1 should not be used
		while(ids.indexOf(Integer.toString(ran)) != -1){	//if ran_number is already used
			ran = rnd.nextInt(1000000000)+2;
		}
		ids.add(Integer.toString(ran));
		a.add(0);
		b.add(0);
		c.add(0);
		return ran;
	}
	
	void set_a(int id,int value){
		if(value < 0) return; //if value is negative
		int id_n = ids.indexOf(Integer.toString(id));	//id_n = id number
		if(id_n == -1 || id == 0) return;	//if id does not exist
		a.set(id_n,value);		
	}
	
	void set_b(int id,int value){
		if(value < 0) return; //if value is negative
		int id_n = ids.indexOf(Integer.toString(id));	//id_n = id number
		if(id_n == -1 || id == 0) return;	//if id does not exist
		b.set(id_n,value);		
	}
	
	void set_c(int id,int value){
		if(value < 0) return; //if value is negative
		int id_n = ids.indexOf(Integer.toString(id));	//id_n = id number
		if(id_n == -1 || id == 0) return;	//if id does not exist
		c.set(id_n,value);		
	}
	
	int get_a(int id){
		int id_n = ids.indexOf(Integer.toString(id));	//id_n = id number
		if(id_n == -1 || id == 0) return -1;	//if id does not exist
		return a.get(id_n);
	}
	
	int get_b(int id){
		int id_n = ids.indexOf(Integer.toString(id));	//id_n = id number
		if(id_n == -1 || id == 0) return -1;	//if id does not exist
		return b.get(id_n);
	}
	
	int get_c(int id){
		int id_n = ids.indexOf(Integer.toString(id));	//id_n = id number
		if(id_n == -1 || id == 0) return -1;	//if id does not exist
		return c.get(id_n);
	}
}