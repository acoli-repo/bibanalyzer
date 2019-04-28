bibanalyzer
===========

Bibliographic reference analysis / Bibtype and bibfield classification

Version of August 5th, 2014
------------------------

Copyright (c) 2014 by Christian Chiarcos, Niko Schenk <br>
Goethe-Universität Frankfurt am Main <br>
Applied Computational Linguistics Lab (ACoLi) <br>
http://acoli.cs.uni-frankfurt.de <br>
Robert-Mayer-Straße 10 <br>
60325 Frankfurt am Main <br>

------------------------




------------------------
- What it is
------------------------
Bibanalyzer adds structure to unstructured (plaintext) bibliographic references 
(i.e. to lists of citations which usually appear at the end of scientific publications).

The program supports the Springer A++ export format (http://devel.springer.de/A++/V2.4/DTD/), as well as 
a custom token-based labeling format, including an HTML visualization of the output.

The core of the program stems from a CRF-driven and a MaxEnt-driven classification component 
(cf. MALLET - Machine Learning toolkit http://mallet.cs.umass.edu/) 



------------------------
- What it does
------------------------
The bibanalyzer project consists of three components:

1. A bibtype classification component. (Maximum Entropy / Naive Bayes Classifier)
   For the analysis, we distinguish between three elementary bibtypes [to be consistent with the Springer A++ XML format]: 
    1. BibArticles, 2. BibBooks, 3. BibChapters. 
    These three roughly correspond to the larger group of 
    1. articles, 2. books and proceedings, 3. incollections and inproceedings in DBLP terminology (http://dblp.uni-trier.de/xml/)

2. A bibfield classification component.  (Conditional Random Fields Sequence Labeling)
   E.g., for more fine-grained tagging of authors, titles, year, etc. (also in line with the Springer A++ labels)
   
3. A separate pdf to text pipeline.
   Which can be used as a preprocessing step towards bibliographic analysis.


The core of the tool is based on * six * separate pretrained statistical models
  3 models trained on DBLP data (http://dblp.uni-trier.de/xml/) -- one for each bibtype.
  3 models trained on Open Access Springer bibliographic references (http://www.springeropen.com/authors/license) -- again, one for each bibtype.
  
The final output delivers * two * separate analyses: one for each of the bibtype-driven, combined outputs of the DBLP and Springer models.

Cf. the separate FIRST_STEPS.txt file for instructions on how to use the program.



------------------------
- How it does what it does
------------------------

This is a simplified procedure of the basic steps that the program performs in order to analyze plaintext references.

1. Read in plaintext references (either from plain text file, or from Springer A++ format [parameterizable])

2. Tokenize the plaintext references according to a custom tokenization technique.

3. Augment each token with additional features, such as whether a token is part of a certain dictionary or not, whether
   it is most likely a year or whether it starts with all upper-case letters, etc.
   
4. Use * six * separate CRF models (three DBLP types (BibArticle, BibBook, BibChapter), as well as three Springer models) to output
   six separate analyses for the input. Note that different models operate on differently augmented tokenizated data. E.g., they
   plaintext models do not need any additional features except the raw tokens. The current setting annotates plaintext data with various
   features (Cf. de.acoli.informatik.uni.frankfurt.de.bibfieldfeatures.TokenizedPlaintextFeatureAnnotation.java)
   
5. Based on other features from the dictionaries and additional features induced from the plaintext references, use the 
   bibtype classification module to predict a certain bibtype for each single reference. (three classes: BibArticle, BibBook, BibChapter)
   
6. Based on the bibtype classification results, combine the six analyses from step 4 into * two * analyses: one for the DBLP models and 
   one for the SPRINGER models.
   
7. Export these two separate analyses to Springer A++ XML format.

8. Export these two separate analyses to an HTML visualization of the labels.



------------------------
- Project structre:
------------------------

- data/ (contains various files which are being generated during the analysis)
    
      a++/ (generated Springer A++ data formats)
	      combined/ (based on the bibtype prediction for each reference, one combined analysis for the DBLP model and one for the Springer model)
	      per_model/ (A++ export format for each of the six separate models) 
	
      bibtypes/ (various data files resulting from bib type classification)
	      bibtypes_predicted.txt (contains the final predicted bibtypes -- 1, 0, -1 / BibArticle, BibBook, BibChapter for each of the single plaint input references)
	
      tagged/ (various data files resulting from bib field classification)
	      combined/
	        (based on the bibtype prediction for each reference, a combined analysis for the DBLP model and one for the Springer model) <br>
	      combined_visualized/
	        (HTML visualization of the combined/ outputs) 
	     per_model/
	      DBLP (per DBLP model token-label analysis of the input) 
	      SPRINGER (per Springer model token-label analysis of the input) 
	     per_model_visualized/
	      (separate HTML visualizations for each of the per_model/ analyses)
      
      tokenized/ (intermediate representations of plaintext references - including feature augmented tokens) <br>
	  input_tok.txt
	    (The input in custom tokenized one-token-per-line format)
	  feature_augmented_DBLP/
	    (input_tok.txt annontated with additional features for the DBLP model as specified by the TokenizedPlaintextFeatureAnnotation.java)
	  feature_augmented_SPRINGER/
	    (input_tok.txt annontated with additional features for the SPRINGER model as specified by the TokenizedPlaintextFeatureAnnotation.java)
	  
- dicts/ (contains the DBLP and SPRINGER dictionaries which drive the analysis)

- dist/ (Command line tool bibanalyzer.jar and javadoc)

- input/
      crf_training/ (everything related to setting up an own CRF model for bibfield classification)
      dumps/ (The raw "gold" annotated data from DBLP and SPRINGER sources)
      maxent_training (everything related to setting up an own MaxEnt model for bibtype classification)
      plaintext_references (input files used as sample for the analysis)
    
- mallet-2.0.7/
  bibanalyzer is based on a CRF model for bibfield classification and a MaxEnt model for bibtype prediction.
  This directory contains the MALLET-related files. (cf. folder for license information).

- models/
  Various model files for CRF bibfield tagging and MaxEnt bibtype prediction.
  The folder contains separate folders for Springer and DBLP models, and moreover, distinguishes between models 
  which have been trained including features, compared to those which are based only on the raw token data.
  Please attribute the use of our models as described in the accompanying license file.

- pdf2text/
    pdfs/ (Store the custom PDF files in this directory)
    txt/ (Inspect the results in this folder)






------------------------
- Licences:
------------------------


The project source code under /src 
  ---> Eclipse Public License, Version 1.0 (EPL-1.0)

Our model files under /models 
  ----> http://opendefinition.org/licenses/odc-by/ 
  Please attribute the use of our models according to the license. (cf. /models directory for more details.)
  
  
External tools and resources:  
  
crf_tagger.jar and /mallet-2.0.7
  ---> Common Public License Version 1.0 (CPL)
  MALLET (http://mallet.cs.umass.edu/sequences.php) / see mallet directory.  
  (NOTE: This license has been superseded by the Eclipse Public License) Eclipse Public License, Version 1.0 (EPL-1.0)

/dicts
  /DBLP (http://dblp.uni-trier.de/xml/README.txt)
  ----> Open Data Commons Attribution License (ODC-BY 1.0) 
  http://opendatacommons.org/licenses/by/summary/
   
  /SPRINGER 
  ----> Open Access Data (Creative Commons License) 
  http://www.springeropen.com/openaccess/whatisopenaccess
  
  
/pdf2xml 
  ---> GNU General Public License version 2.0 (GPLv2)
  (http://sourceforge.net/projects/pdf2xml/) 
  


  
------------------------
- Current TODOs:
------------------------

- SPRINGER models under (models/SPRINGER_trained_features) need to be replaced with better models. (Currently they are trained on only 2,000 instances [references] for each bibtype).
- Feature annotation for tokenized references based on the dictionaries (located under /dicts) is very slow, especially for large input files.
- Feature annotation for other low-level features, such as "isYear", or "isUpperCase" should be made parameterizable via external text files, not via regular expressions within the code.
- Source code is not thread-safe in combination with the web service.
- pdf2text pipeline currently only works under Linux environments. TODO: Make it available for Windows, as well.
- Currently the program outputs two different analyses: one based on the DBLP model (data/tagged/combined/DBLP) and one which is based on the SPRINGER data trained model (data/tagged/combined/SPRINGER).
  The idea is 
  1st) to combine the output of the two models and see (maybe heuristically) which one delivers the better analysis for a given set of references.
  and
  2nd) to see if a cascade of models could be used to find a "best" analysis.
