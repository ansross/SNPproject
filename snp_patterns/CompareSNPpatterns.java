package snp_patterns;
import java.util.Vector;
import java.util.HashMap;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.System;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class CompareSNPpatterns{
	private HashMap<String, SNPCompare> sampleCompare = new HashMap<String,SNPCompare>();
	public static void main(String [] args){
		int num_files = 0;
		Vector<String> pattern_files = new Vector<String>();
		String outfile = null;
		for(int i=0; i<args.length; ++i){
			if(args[i].equals("-N")){
				num_files = Integer.parseInt(args[++i]);
			}
			else if(args[i].equals("-F")){
				//must define number of files before providing files
				if(num_files == 0){
					break;
				}
				for(int fileCount=0; fileCount<num_files;++fileCount){
					pattern_files.add(args[++i]);
				}
			}
			else if(args[i].equals("-O")){
				outfile = args[++i];
			}
		}	
		if(num_files==0 || pattern_files.isEmpty() || outfile==null){
			System.out.println("Improper arguments or wrong order");
			System.out.println("java snp_pattern.CompareSNPpatterns -N <number_of_files> -F <N file names> -O <output file name>");
			System.exit(-1);
		}	
		String[] SAM_file_names = new String[num_files];
		for(int fileIter=0; fileIter<pattern_files.size(); ++fileIter){
			SAM_file_names[fileIter] = parseFile(pattern_files.get(fileIter), fileIter);
		}
		if(true){
			for(int i=0; i<SAM_file_names.length; ++i){
				System.out.println(SAM_file_names[i]);
			}
		}	
		
	}

	private static String parseFile(String file_name, int index){
		System.out.println("Parsing file "+index+": "+file_name);
		String SAM_file=""; 
		final Charset ENCODING = StandardCharsets.UTF_8;
		Path file_path = Paths.get(file_name);
		try(Scanner scanner = new Scanner(file_path, ENCODING.name())){
			int line_num = 0;
			String line;
			while(scanner.hasNextLine()){
				line = scanner.nextLine();
				if(line.charAt(0)=='@'){
					String [] comment_tokens = line.split(" ");
					if(comment_tokens[1].equals("SAM")){
						SAM_file = comment_tokens[comment_tokens.length-1];								
					}
					continue;
				}	
				

			}
		}
		catch(IOException e){
			System.out.println(e);
		}
		if(SAM_file.equals("")){
			System.out.println("ERROR with pattern file: "+file_name+". No SAM file header");
		}
		return SAM_file;
	}	

	class SNPCompare{
		SNP snp;
		int[] counts;

		public SNPCompare(int num_files, SNP snp_arg){
			counts = new int[num_files];
			snp = snp_arg;
		} 
	}
}

