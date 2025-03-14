#!/bin/bash
jobs=$(dirname "${BASH_SOURCE[0]}")
#jobs="/home/fran/mapoptics/jobs/"

fasta="$1"
fasta_path="$(dirname "${fasta}")"

fasta_name="$(basename "${fasta}")"
fasta_name=${fasta_name%.*}
job_name=${fasta_path%/*}

echo "$jobs""/""$job_name""/""Results"

script="fa2cmap_multi_color.pl"
enzymes=("GCTCTTC" "CCTCAGC" "GAATGC" "GCAATG" "ATCGAT" "CTTAAG" "CACGAG")
site_density=0;
density=0;
best_enzyme=""
cd "$jobs""/../solve/tools/pipeline/Solve3.7_03302022_283/HybridScaffold/03302022/scripts/" || exit
for enz in "${enzymes[@]}";
do
  perl $script -i "../../../../../../../jobs/""$fasta" -e "$enz" 1
done
echo "fa2cmap multi color done"
echo "$fasta"
echo "$jobs""$fasta"

cd ../../../../../../../jobs
echo $PWD
cd "$jobs""/""$fasta_path" || exit
echo $PWD
#touch "$jobs""/""$fasta""_compare_enzymes.txt";
touch "$fasta_name""_compare_enzymes.txt";
for enz in "${enzymes[@]}";
do
  density=$(grep -F 'Channel 1 site density (sites/100kbp)' "$fasta_name""_""$enz""_0kb_0labels_summary.txt" | awk '{split($0,l,":"); print $6}')
  echo "$enz: $density" >> "compare_enzymes.txt"
  if [[ "$density" > "$site_density" ]]
    then
      site_density=$density
      best_enzyme=$enz
    fi
done
echo "$best_enzyme"

## Move resulting file to Results folder
mv "compare_enzymes.txt" "..//Results"
