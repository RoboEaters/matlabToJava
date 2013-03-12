// Corresponds with MATLAB script called "create_posecell_weights.m"

public class Posecells 
{
	final double PI = Math.PI;
	double dim_center = 0;
	double[][][] weights = null;
	double total = 0;
	
	// Constructor to initialize posecell weight array 
	// and the center of the dimension
	public Posecells(int dim, int var)
	{
		dim_center = Math.floor(dim / 2.0) + 1;
		weights = new double[dim][dim][dim];	
	}
	
	// Create array of posecell weights
	public double[][][] create_posecell_weights(int dim, int var)
	{
		// Creates a 3D normal distribution based on given dimension and variance
		
		// Create 3-dimensional array of posecell weights
		double[][][] weight = new double[dim][dim][dim];
		
		// Create constant a
		double a = Math.sqrt(2 * PI);
		
		// Set up weights for posecells
		// Sum up elements of array for total weight
		double total = 0;
		for (int x = 0; x < dim; x++)
		{
			for (int y = 0; y < dim; y++)
			{
				for (int z = 0; z < dim; z++)
				{
					double b = -1 * (x - dim_center)*(x-dim_center);
					double c = (y - dim_center) * (y - dim_center);
					double d = (z - dim_center) * (z - dim_center);
					weight[x][y][z] = 1/(var * a * Math.exp((b - c - d) / (2 * (var * var))));
					total += weight[x][y][z];
				}
			}
		}

		// Divide each weight by the total weight
		for (int x = 0; x < dim; x++)
		{
			for (int y = 0; y < dim; y++)
			{
				for (int z = 0; z < dim; z++)
				{
					weight[x][y][z] = weight[x][y][z]/total;
				}
			}
		}	
		
		return weight;
	}
}
