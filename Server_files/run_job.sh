#!/bin/bash
#Fran Peters
#this script requires 
#Perl
#FaNDOM
#RefAligner
#runBNG
#path to required programs
digest="/home/fran/mapoptics/solve/tools/pipeline/Solve3.7_03302022_283/HybridScaffold/03302022/scripts/fa2cmap_multi_color.pl"
fandom="/home/fran/mapoptics/FaNDOM/"
refaligner="/home/fran/mapoptics/solve/tools/pipeline/Solve3.7_03302022_283/RefAligner/12432.12542rel/RefAligner"
runBNG="/home/fran/mapoptics/runBNG/runBNG"

while test $# -gt 0; 
    do
           case "$1" in
                 -h|--help)
                    echo "__________________________________________________________"
                    echo "  __  __                 ____          _    _             "
                    echo " |  \/  |               / __ \        | |  (_)            "
                    echo " | \  / |  __ _  _ __  | |  | | _ __  | |_  _   ___  ___  "
                    echo " | |\/| | / _  ||  _ \ | |  | ||  _ \ | __|| | / __|/ __| "
                    echo " | |  | || (_| || |_) || |__| || |_) || |_ | || (__ \__ \ "
                    echo " |_|  |_| \__,_||  __/  \____/ |  __/  \__||_| \___||___/ "
                    echo "                | |            | |                        "
                    echo "                |_|            |_|                        "
                    echo ""
                    echo "Welcome to MapOptics fasta to xmap alignment sript"
                    echo "Command line argumnents:"
                    echo "-h,--help"
                    echo "Brings up this help message and exits program"
                    echo "-j,--job"
                    echo "Provides the name of the job folder the alignment is linked to"
                    echo "-r,--ref"
                    echo "The name of the uploaded reference fasta file";
                    echo "-q,--qry"
                    echo "The name of the uploaded query fasta/cmap file"
                    echo "-e,--enz"
                    echo "The requested enzyme to perform in silico digestion with"
                    echo "If user provides keywork 'calculate' the optimum enzyme will be calculated"
                    echo "-a,--align"
                    echo "Accepted values are either 'fandom' or 'refaligner'"
                    echo "Allows user to change which alignment algorithm is used"
                    echo "__________________________________________________________"
                    exit 0
                    ;;
                -j|--job)
                    shift
                    job=$1
                    if [ ! -d "/home/fran/mapoptics/jobs/$job" ] 
                      then
                          echo "Directory for Job: $job does not exist." 
                          exit 2;
                      fi
                    out="/home/fran/mapoptics/jobs/$job/Files/Results"
                    ref_folder="/home/fran/mapoptics/jobs/$job/Files/Reference/"
                    qry_folder="/home/fran/mapoptics/jobs/$job/Files/Query/"
                    shift
                    ;;
                -r|--ref)
                    shift
                    ref="/home/fran/mapoptics/jobs/$job/Files/Reference/$1"
                    ref_path="$(dirname "${ref}")"
                    ref_basename="$(basename "${ref}")"
                    ref_basename=${ref_basename%.*}
                    if [[ ! -f "$ref" ]]
                    then
                          echo "The reference file $1 has not been found"
                          exit 2;
                    fi
                    shift
                    ;;
                -q|--qry)
                    shift
                    qry="/home/fran/mapoptics/jobs/$job/Files/Query/$1"
                    qry_path="$(dirname "${qry}")"
                    qry_basename="$(basename "${qry}")"
                    qry_basename=${qry_basename%.*}
                    if [[ ! -f "$qry" ]]
                      then
                          echo "The reference file $1 has not been found"
                    fi
                    shift
                    ;;
                -e|--enz)
                    shift
                    if [[ $1 == *"calculate"* ]]; then
                      echo "calculate has been called"                       
                        enzyme=$(sh ./calc_best_enz.sh "$ref")
                    else
                    enzyme=$1
                    fi
                    
                    shift
                    ;;
                -a|--align)
                    shift
                      if [[ $1 == *"fandom"* ]] 
                        then
                        aligner=$1
                        
                      elif [[ $1 == *"refaligner"* ]]
                        then
                        aligner=$1
                      else
                        echo "Accepted alignment values are either fandom or refaligner"
                          exit 1;
                      fi
                      
                    shift
                    ;;
                *)
                   echo "$1 is not a recognized flag!"
                   exit 1;
                   ;;
          esac
  done  

#Create the log file and add the start time 
touch /home/fran/mapoptics/jobs/"$job"/log.txt;
echo "Start: $(date)" >> /home/fran/mapoptics/jobs/"$job"/log.txt;

if [[ $ref == *".gz"* ]]
    then
    gunzip "$ref"
fi
#check the reference is in fasta format 
if [[ $ref == *.fa ]]
  then    
    #generate the karyotype file
   
    samtools faidx "$ref"
    awk -F "\t" 'OFS=" " {print $2, $1 }' "$ref".fai > "$out"/karyotype.txt
    # calculate the genome size in Mbp
    genomesize=$(awk '{sum+=$2}END{print sum*0.000001}' "$ref".fai)
    # digest the ref into a cmap 
    perl $digest -i "$ref" -o "$ref_path" -e "$enzyme" 1 
   
    echo "Status: Reference Digested"  >> /home/fran/mapoptics/jobs/"$job"/log.txt;
elif [[ $ref == *.fasta ]]
  then    
    #generate the karyotype file
    samtools faidx "$ref"
    awk -F "\t" 'OFS=" " {print $2, $1 }' "$ref".fai > "$out"/karyotype.txt
    # calculate the genome size in Mbp
    genomesize=$(awk '{sum+=$2}END{print sum*0.000001}' "$ref".fai)
    # digest the ref into a cmap 
    perl $digest -i "$ref" -o "$ref_path" -e "$enzyme" 1 
   
    echo "Status: Reference Digested"  >> /home/fran/mapoptics/jobs/"$job"/log.txt;
elif [[ $ref == *.fna ]]
  then
    
    #generate the karyotype file
    samtools faidx "$ref"
    awk -F "\t" 'OFS=" " {print $2, $1 }' "$ref".fai > "$out"/karyotype.txt
    # calculate the genome size in Mbp
    genomesize=$(awk '{sum+=$2}END{print sum*0.000001}' "$ref".fai)
    # digest the ref into a cmap 
    perl $digest -i "$ref" -o "$ref_path" -e "$enzyme" 1 
  
    echo "Status: Reference Digested"  >> /home/fran/mapoptics/jobs/"$job"/log.txt;
else 
  echo "reference not in required format"
fi

  
if [[ $qry == *".gz"* ]]
    then
    gunzip "$qry"
fi   
  
#check the query is in fasta format
if [[ $qry == *.fa ]] 
  then    
    
    # digest the qry into a cmap 
   
    perl $digest -i "$qry" -o "$qry_path" -e "$enzyme" 1 
    echo "Status: Query Digested"  >> /home/fran/mapoptics/jobs/"$job"/log.txt;
elif [[ $qry == *.fna ]]
  then   
    # digest the qry into a cmap 
    perl $digest -i "$qry" -o "$qry_path" -e "$enzyme" 1 
   
    echo "Status: Query Digested"  >> /home/fran/mapoptics/jobs/"$job"/log.txt;
    elif [[ $qry == *.fasta ]]
  then 
    # digest the qry into a cmap 
    perl $digest -i "$qry" -o "$qry_path" -e "$enzyme" 1
  
    echo "Status: Query Digested"  >> /home/fran/mapoptics/jobs/"$job"/log.txt;
elif  [[ "$qry" != *.cmap ]]
    then
      echo "Query file not in expected format"
      exit 1;
else
    echo "somethings gone wrong 1"
fi


echo "Status: Aligning data using $aligner" >> /home/fran/mapoptics/jobs/"$job"/log.txt;

if [[ "${aligner}" == *"fandom"* ]]
  then

  cd $fandom || exit
  python PythonScript/wrapper_contigs.py -f "$PWD" -t 10 -r "$ref_folder""$ref_basename""_GCAATG_0kb_0labels.cmap" -q "$qry_folder""$qry_basename""_GCAATG_0kb_0labels.cmap" -n "$job" -o "$ref_folder" -c nh -m 1 
  cd "$ref_folder" || exit
  mv  "$job""_final_alignment.xmap" "$job"".xmap"
  mv  "$job"".xmap" "$out"
elif [[ "${aligner}" == *"refaligner"* ]]
  then
    cd $runBNG || exit
    
    ./runBNG compare -R $refaligner -r "$ref_folder""$ref_basename""_GCAATG_0kb_0labels.cmap" -q "$qry_folder""$qry_basename""_GCAATG_0kb_0labels.cmap" -z "$genomesize" -t 10 -m 100 -p "$job" -o "$ref_folder"
    cd "$ref_folder || exit" || exit
    mv  "$job"".xmap" "$out"
else
  echo "somethings gone wrong"
  exit 2;
fi

#move all the relevent files to the results directory

cd "$ref_folder" || exit
mv "$ref_basename""_""$enzyme""_0kb_0labels.cmap" "$job""_ref.cmap"
mv "$job""_ref.cmap" "$out"

cd "$qry_folder" || exit
mv "$qry_basename""_""$enzyme""_0kb_0labels.cmap" "$job""_qry.cmap"
mv "$job""_qry.cmap" "$out"

echo "Status: Completed" >> /home/fran/mapoptics/jobs/"$job"/log.txt;
echo "----------------------------------"
echo "End of pipeline. Results are in $out";
exit 1;