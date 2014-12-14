package snp_patterns;
import java.util.Vector;
import java.util.HashMap;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.System;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class ComparePatternFreqs{
	int num_files;	
	HashMap<String, SNPCompare> compare_samples = new HashMap<String,SNPCompare>();
	Vector<String> pattern_files;
	String [] SAM_file_names;	

	public ComparePatternFreqs(int num_files_arg, Vector<String> pattern_files_arg){
		num_files=num_files_arg;
		pattern_files = pattern_files_arg;
		SAM_file_names = new String[num_files];
	}

	public void printFrequencies(String outfile){
		try{
			System.out.println("Printing results to: "+outfile);
			PrintWriter writer = new PrintWriter(outfile);
			writer.print("reference gene\tpattern\t");
			for(String file: SAM_file_names){
				writer.print(file+"\t");
			}
			writer.println();
			for(String id: compare_samples.keySet()){
				writer.println(compare_samples.get(id).to_print());
			}
			writer.close();
			System.out.println("Wrote output");
		}catch(FileNotFoundException e){
		}
	}
	
	public void parsePatternFiles(){	
		for(int fileIter=0; fileIter<pattern_files.size(); ++fileIter){
			SAM_file_names[fileIter] = parseFile(compare_samples, pattern_files.get(fileIter), fileIter);
		}
		if(true){
			for(int i=0; i<SAM_file_names.length; ++i){
				System.out.println(SAM_file_names[i]);
			}
		}	
		
	}

	private String parseFile(HashMap<String, SNPCompare> compare_samples, String file_name, int file_num){
		System.out.println("Parsing file "+file_num+": "+file_name);
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
				String [] pattern_tokens = line.split("\t");
				String ref_gene = pattern_tokens[0];
				String pattern = pattern_tokens[1];
				int count = Integer.parseInt(pattern_tokens[2]);
				String id = ref_gene+pattern;
				if(!compare_samples.containsKey(id)){
					SNPCompare new_snp = new SNPCompare(num_files, ref_gene, pattern);
					compare_samples.put(id, new_snp);
				}
				SNPCompare comp = compare_samples.get(id);
				comp.incrementCount(file_num);			
				

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
		String ref_gene;
		String pattern;
		int[] counts;


		public SNPCompare(int num_files, String gene_arg, String pattern_arg){
			counts = new int[num_files];
			ref_gene = gene_arg;
			pattern = pattern_arg;
		}
	
		public String to_print(){
			String ret_string = ref_gene+"\t"+pattern+"\t";
			for(int i=0; i<counts.length;++i){
				ret_string = ret_string +counts[i]+"\t";
			}
			return ret_string;
		}

		public void incrementCount(int file_num){
			counts[file_num] += 1;
		} 
	}
}

