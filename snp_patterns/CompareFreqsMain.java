package snp_patterns;
import java.util.Vector;


class CompareFreqsMain{
        public static void main(String [] args){
                int num_files = 0;
                Vector<String> pattern_files = new Vector<String>();
                String outfile = null;
                for(int i=0; i<args.length; ++i){
                        if(args[i].equals("-N")){
                                num_files = Integer.parseInt(args[++i]);
                        }
                        else if(args[i].equals("-F")){
                                must define number of files before providing files
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
		ComparePatternFreqs comp_freq = new ComparePatternFreqs()
	}
}

