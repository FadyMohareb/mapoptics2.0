#!/bin/bash
fasta=$1
fasta_path="$(dirname "${fasta}")"
fasta_name="$(basename "${fasta}")"
fasta_name=${fasta_name%.*}
script="fa2cmap_multi_color.pl"
enzymes=("GCTCTTC" "CCTCAGC" "GAATGC" "GCAATG" "ATCGAT" "CTTAAG" "CACGAG")
site_density=0;
density=0;
best_enzyme=""
cd "/home/fran/mapoptics/solve/tools/pipeline/Solve3.7_03302022_283/HybridScaffold/03302022/scripts/" || exit
for enz in "${enzymes[@]}";
do
  perl $script -i "$fasta" -e "$enz" 1
done

cd "$fasta_path" || exit
touch "$fasta"_compare_enzymes.txt;
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
