/***
* Name: Matrices
* Author: Benoit Gaudou
* Description: Examples of the syntax and various operators used to manipulate the 'matrix' data type. 
* Read the comments and run the model to get a better idea on how to use matrices in GAML. 
* Tags: matrix, loop
***/

model Matrices

species declaring_matrix_attributes {
	
	/**
	 * Declarations of matrix attributes
	 */
	 // The simplest declaration identifies empty_matrix as a matrix that can contain any type of objects. 
	 // Its default value will be [] (the empty matrix) if it is not initialized.
	matrix empty_matrix;
	// To provide it with an initial value, use the '<-' (or 'init:') facet
	matrix explicit_empty_matrix <- [];
	// matrices can also be provided with a default size, in which case they are filled with a given element
	matrix matrix_of_size_3_3_with_0 <- {3,3} matrix_with 0; // => [[0,0,0],[0,0,0],[0,0,0]]
	// matrices can be declared so that they only accept a given type of contents.
	// For instance, empty_matrix_of_int will only accept integer elements
	matrix<int> empty_matrix_of_int ;	
	// matrices can be define explicitely by rows
	 matrix<int> matrix_explicit <- matrix([[1,2,3],[9,8,7]]);	
	// the value passed to 'matrix_with' is verified and casted to the contents type of the matrix if necessary
	matrix<int> matrix_of_int_size_3_3_filled_with_string<- matrix<int>({3,3} matrix_with('1')); // matrix_of_int_size_3_3_filled_with_string is filled with the casting of '1' to int, i.e. 1
	matrix<string> matrix_of_string_size_3_3_filled_with_string <- {3,3} matrix_with('1'); // while matrix_of_string_size_3_3_filled_with_string is filled with the string '1'
	// the casting is also realized if the matrix is initialized with a value
	matrix<int> matrix_of_int_with_init_of_string <- matrix<int>([['10', '20'],['30','40']]); // => [[10,20],[30,40]]
	matrix<float> matrix_of_float_with_init_of_string <- matrix<float>(matrix_of_string_size_3_3_filled_with_string); // => [[1.0,1.0,1.0],[1.0,1.0,1.0],[1.0,1.0,1.0]]
	// When the casting is not obvious, the default value is used
	matrix<float> matrix_of_float_with_impossible_casting <- matrix<float>([['A','B'],['C','D']]);   // => [[0.0,0.0],[0.0,0.0]]
	// matrices can of course contain lists
	matrix<list> matrix_of_lists <- matrix<list>({5,5} matrix_with [1,2]);
	// matrices can of course contain matrices
	matrix<matrix> matrix_of_matrices <- matrix<matrix>({5,5} matrix_with matrix([[1],[2]])) ;
	// untyped matrixs can contain heterogeneous objects
	matrix untyped_matrix <- matrix([['5',5],[5,true]]);
	// the casting applies to all elements when a contents type is defined (note the default last value of 0)
	matrix<int> recasted_matrix_with_int <- matrix<int>(untyped_matrix); //=> [[5,5],[5,1]]
	// Matrices can also been created from other types, such as a list
	matrix<string> matrix_of_string_from_list_3_2 <- ['A','B','C','D','E','F','G'] as_matrix {3,2};
	// When the requested dimension exceeds the number of available elements, the empty cells are filled with a nil value.
	matrix<string> matrix_of_string_from_list_3_3 <- ['A','B','C','D','E','F','G'] as_matrix {3,3};	
	
	init {
		write "";
		write "== DECLARING MATRICES ==";
		write "";
		write sample(empty_matrix);
		write sample(explicit_empty_matrix);
		write sample(matrix_of_size_3_3_with_0);
		write sample(empty_matrix_of_int);
		write sample(matrix_explicit);
		write sample(matrix_of_int_size_3_3_filled_with_string);
		write sample(matrix_of_string_size_3_3_filled_with_string);
		write sample(matrix_of_int_with_init_of_string);
		write sample(matrix_of_float_with_init_of_string);
		write sample(matrix_of_float_with_impossible_casting);
		write sample(matrix_of_lists);
		write sample(matrix_of_matrices);
		write sample(untyped_matrix);
		write sample(recasted_matrix_with_int);
		write sample(matrix_of_string_from_list_3_2);		
		write sample(matrix_of_string_from_list_3_3);
		write "";

		// Matrices are not always declared litterally and can be obtained from various elements
		// by using the casting 'matrix()' operator
		// for instance, matrix(species_name) will return a matrix of all the instances of species_name (as a matrix of one row.
		create test_species number:5;
		matrix<test_species> matrix_my_agents <- matrix(test_species);
		write sample(matrix_my_agents);
//		matrix<test_species> matrix_my_agents_2_2 <- test_species as_matrix {2,2};
//		write sample(matrix_my_agents_2_2);
		// A matrix can also been get from a csv_file (more specifically from its contents)		
		file my_csv_file <- csv_file("includes/iris_small.csv",",",float,true);
		matrix<float> matrix_from_csv_file <- matrix<float>(my_csv_file.contents);
		write sample(matrix_from_csv_file);
 	}

}

species test_species {}

species accessing_matrix_elements {
	matrix<int> m1 <- matrix([[1,2,3,4],[5,6,7,8]]);
	matrix<string> m2 <- ['this','is','a','matrix', 'of','strings'] as_matrix {3,2};
	matrix<int> m_square <- matrix([[1,2,8],[4,12,6],[7,8,9]]);	
	
	init {
		write "";
		write "== ACCESSING MATRIX ELEMENTS ==";
		write "";
		write sample(m1);
		write sample(m2);
		write sample(first(m1));
		write sample(last(m1));
		// Matrices are indexed by a point 
		write sample(m1 at {0,0});
		write sample(m1[{1,3}]);
		write sample(length(m1));
		write sample(mean(m1));
		write sample(max(m1));
		write sample(min(m1));
		write sample(any(m1));
		write sample(3 among m2);
		write sample(m1 contains 1);
		write sample(m1 contains_all [1,4,6, 14]);
		write sample(m1 contains_any [1,23]);
		// reverse on a matrix is a transpose
		write sample(reverse(m2));
		// collect transforms a matrix into a list
		write sample(m1 collect (each + 1));
		// we thus need to transform again the result list to get a matrix
		write sample(m1 collect (each + 1) as_matrix {2,4});
		write sample(m1 collect (norm({each, each, each})));
		write sample(m1 where (each > 5));
		write sample(m1 count (each > 5));
		write sample(m1 group_by (even(each)));
		write sample(m2 index_by (each + "_index"));
		write sample(m1 index_of 2);
		write sample(m2 last_index_of 'is');
		write sample(m2 sort_by each);
		write sample(m2 sort_by length(each));
		write sample(m2 first_with (first(each)  = 'o'));
		write sample(m2 where (length(each) = 2) );
		write sample(m2 with_min_of (length(each)));
		write sample(m2 with_max_of (length(each)));
		write sample(m2 min_of (length(each)));
		write sample(m2 max_of (length(each))); 
		write sample(m2 as_map (length(each)::"new"+each));
		
		// Rows (resp. columns) can also be accessed
		write sample(columns_list(m1));
		write sample(rows_list(m1));		
		write sample(m1 row_at 1);
		write sample(m1 column_at 1);
		
		// Some classical operators of matrix computation have been introduced
		write sample(det(m_square));
		write sample(determinant(m_square));
		write sample(eigenvalues(m_square));
		write sample(inverse(m_square));
		write sample(trace(m_square)); 
		write sample(transpose(m_square));		
	}
}

species combining_matrices {
	matrix<int> m1 <- matrix([[1,2,3],[4,5,6],[7,8,9]]);
	matrix<int> m2 <- [1,3,5,7,9,11] as_matrix {3,2};
	
	init {
		write "";
		write "== COMBINING MATRICES ==";
		write "";
		write sample(m1);
		write sample(rows_list(m1));
		write sample(m2);
		write sample(rows_list(m2));		
		write sample(m1 + m1);
		write sample(m2 - m2);
		// inter between 2 matrices returns the list of all the elements part of both matrices.
		write sample(m1 inter m2);
		// union between 2 matrices returns the list of all the elements part at least in one of the two matrices.
		write sample(m1 union m2);
		write sample(interleave ([m1,m1]));
		matrix<string> m3 <- matrix<string>(m1 + m1);
		write "matrix<string> m3 <- m1 + m2; " + sample(m3);
		write sample(m1 as list<float>);
		write sample(2 * m1);
		write sample(2 + m1);
		
		write sample(m1 * m1);
		write sample(m1 / m1);
		
		// Multiplication between matrices : * is the product element by element, whereas . is the matrices multiplication
		write sample(m1 . m1);
		write sample(m2 . transpose(m2));
				
		write sample(m1 append_horizontally m1);			
		// Notice that when the 2 matrices do not have the same number of rows, m2 is considered as being 3x3.
		// The matrix is completed by 0 and thus becomes matrix<int>([[1,9,0],[7,5,0],[3,11,0]])
		write sample(m1 append_horizontally m2);
		// m1 dimension is set to the m2 dimension
		write sample(m2 append_horizontally m1);		
		write sample(m1 append_vertically m1);
		write sample(m1 append_vertically m2);

		
		// Some combinations of matrices are not possible
		write "Following computations have errors due to incompatible sizes";
		try {
			write sample(m1 + m2);			
		} catch { 
			write "m1 + m2 : " + m1 + " + " + m2 + " are not compatibble to sum.";
		}
		try {
			write sample(m2 . m2);			
		} catch { 
			write "m2 . m2 : " + m2 + " . " + m2 + " are not compatibble to multiply (in the sense of the matrices multiplication).";
		}		
	}
}

species looping_on_matrices {
	init {
		write "";
		write "== LOOPING ON MATRICES ==";
		write "";
		// Besides iterator operators (like "collect", "where", etc.), which provide 
		// functional iterations (i.e. filters), one can loop over matrices using the imperative
		// statement 'loop'
	    matrix<string> matrix_of_strings <- matrix([["A","matrix"],["of","strings"]]);
		write sample(matrix_of_strings);
		
		int i <- 0;
		// Here, the value of 's' will be that added to each element of the matrix
		loop s over: matrix_of_strings {
			i <- i + 1;
			write "Word #" + i + ": " + s;
		}

		// 'loop' can also directly use two integer indices (remember matrices have a zero-based index)
		loop index_row from: 0 to: matrix_of_strings.rows - 1 {
			loop index_column from: 0 to: matrix_of_strings.columns - 1 {
				write "The element at row: " + (index_row+1) + " and column: " + (index_column+1) + " of the matrix is: " + matrix_of_strings[index_column,index_row];				
			}
		}		
	}
}

species modifying_matrices {
	init {
		write "";
		write "== MODIFYING MATRICES ==";
		write "";
		trace {
			// Besides assigning a new value to a matrix, matrices can be manipulated using
			// the "put" statements. 
			// Notice that they have a fix size (number of elements). 
			// As a consequence, add and remove cannot be used on a matrix.
		    matrix<string> matrix_of_strings <- matrix([["A","matrix"],["of","strings"]]);
			write sample(matrix_of_strings);
			put "Two" in: matrix_of_strings at: {0,0};	
			put "matrices" in: matrix_of_strings at: {0,1};			
			write sample(matrix_of_strings);
			
			// The two previous put called can be replaced bby an assignement 
			// Let revert the previous modifications
			matrix_of_strings[{0,0}] <- "A";	
			matrix_of_strings[{0,1}] <- "matrix";	
			
			write sample(matrix_of_strings);
			
			// All the values can also be replaced
			// Let set all the values in the matrix to empty string
			put "" in: matrix_of_strings all: true;
			write sample(matrix_of_strings);
		}
	}
}

experiment Matrices type: gui {
	user_command "Declaring matrices" {create declaring_matrix_attributes;}	
	user_command "Accessing matrix elements " {create accessing_matrix_elements;}	
	user_command "Combining Matrices " {create combining_matrices;}	
	user_command "Modifying Matrices" {create modifying_matrices;}
	user_command "Looping on Matrices " {create looping_on_matrices;}
}