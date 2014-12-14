package snp_patterns;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.System;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
class SNPmain{
	//public static boolean DEBUG = true;
	//args: -d database file
	//      -S SAM file
	//TODO: FIX IOEXCEPTION!!! 
	public static void main(String [] args) throws IOException{
		String database_file=null;
		String SAM_file=null;
		String outfile = null;
		for(int i=0; i<args.length; ++i){
			if(args[i].equals("-D")){
				database_file = args[++i];
			}
			else if(args[i].equals("-S")){
				SAM_file = args[++i];
			}
			else if(args[i].equals("-O")){
				outfile=args[++i];
			}
		}
		//TODO: FIX THIS MESSAGE
		if(database_file==null || SAM_file==null || outfile==null){
			//TODO print and exit
			System.out.println("Improper arguments\n java snp_pattern.SNPcounter -D <database_file> -S <sam_file> [-O <outfile>]");				
			System.exit(-1);
		
		}
		HashMap<String, String> reference = make_ref_map(database_file);	
		//debug print contents of database	
		if(false){
			for(Map.Entry<String, String> entry: reference.entrySet()){
				System.out.println(entry.getKey()+ ": \n"+entry.getValue());
			}
		}
		SAMobject sam_object = new SAMobject(SAM_file, reference, outfile);
		sam_object.parseSAMfile();

		
	}

	public static HashMap<String, String> make_ref_map(String database_filename) throws IOException{

		boolean DEBUG = false;
		System.out.println("Readin in database");

		final Charset ENCODING = StandardCharsets.UTF_8;
		HashMap<String, String> reference_map = new HashMap<String, String>();
		Path database_path = Paths.get(database_filename);	
		try(Scanner scanner = new Scanner(database_path, ENCODING.name())){
				String ref_ID = null;
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				if(line.charAt(0)=='>'){
					//remove > from first element
					ref_ID = line.split(" ")[0].substring(1);
					if(DEBUG){
						System.out.println("added ref: " + ref_ID);
					}

				}
				else{
					String sequence = line;
					if(ref_ID != null){
						reference_map.put(ref_ID, sequence);		 			
					}
					else{
						System.out.println("ERROR IN DATABASE FILE, sequence before ID");
					}
				}
			}

		}
		return reference_map;
	}
}
