# Author: Shubhanshu Mishra <smishra8@illinois.edu>
# Copyright 2015 University of Illinois Board of Trustees, All rights reserved
# Add Weka >3.7.12 to your class path
# Make sure Java >1.7 is installed.
# First process your CSV file using SAIL with Predicted option checked.
# Copy the *.arff file from your ourputDir/original folder.
# Call the file as train_model.sh <Labeled_processed_data.arff>
# Your model will be saved as All_filtered_pos_SGD_10000_3.model in this folder.
# You can look at the training output in the file train_model_pos_SGD_P_NoStop_10000_3.out

java weka.filters.unsupervised.attribute.Remove -R "3,4,6,10-12,14,42,43,45-last" -i "$1" -o All_filtered_pos.arff

# Train with 10000 words per class and min count of 3
java -Xmx10g -Xms10g weka.classifiers.meta.FilteredClassifier -c "last" -threshold-file EVAL_All_filtered_pos_SGD_10000_3.csv -threshold-label "positive" -t All_filtered_pos.arff -d All_filtered_pos_SGD_10000_3.model -F "weka.filters.unsupervised.attribute.StringToWordVector -R 5 -P \"ngram= \" -W 10000 -prune-rate -1.0 -N 0 -L -stopwords-handler weka.core.stopwords.Null -M 3 -tokenizer \"weka.core.tokenizers.NGramTokenizer -max 2 -min 1\"" -W weka.classifiers.functions.SGD -- -F 1 > train_model_pos_SGD_P_NoStop_10000_3.out

