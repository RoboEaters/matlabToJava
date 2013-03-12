// Divide methods to different files
// Work on structs and other stuff

public class main_ratslam 
{
	// My Constants
	static final double PI = Math.PI;

	// Pose cell activity network constraints
	static int PC_DIM_XY = 61;
	static int PC_DIM_TH = 36;
	static int PC_W_E_DIM = 7;
	static int PC_W_I_DIM = 5;
	static double PC_GLOBAL_INHIB = 0.00002;
	static double PC_VT_INJECT_ENERGY = 0.1;
	static int PC_W_E_VAR = 1;
	static int PC_W_I_VAR = 2;

	// Posecell excitation and inhibition 3D weight matrices
	Posecells PC_W_EXCITE = new Posecells(PC_W_E_DIM, PC_W_E_VAR);
	Posecells PC_W_INHIB = new Posecells(PC_W_I_DIM, PC_W_I_VAR);

	// Convenience constants
	double PC_W_E_DIM_HALF = Math.floor(PC_W_E_DIM / 2);
	double PC_W_I_DIM_HALF = Math.floor(PC_W_I_DIM / 2);
	double PC_C_SIZE_TH = (2 * PI) / PC_DIM_TH;

	// Lookups to wrap the pose cell excitation/inhibition weight steps
	double PC_E_XY_WRAP;
		// continue later

	// Lookups for finding the center of the posecell Posecells by rs_get_posecell_xyth()
	double PC_XY_SUM_SIN_LOOKUP;
		// continue later 

	// Specify the movie and the frames to read
	int START_FRAME = 1;
	int END_FRAME = 100; 		// Should be size of viddata (photo, in our case)
	int IMAGE_Y_SIZE = 100;		// Should be size of viddata
	int IMAGE_X_SIZE = 100;		// Should be size of viddata

	
	
	static int[][][] Posecells =  new int[PC_DIM_XY][PC_DIM_XY][PC_DIM_TH];
	
	// Main method of ratSLAM, not including constants (which are above, for the most part)
	public static void main(String[] args) 
	{
		// Set initial position in the pose network
		int x_pc = (int) (Math.floor(PC_DIM_XY / 2.0) + 1);
		int y_pc = (int) (Math.floor(PC_DIM_XY / 2.0) + 1);
		int th_pc = (int) (Math.floor(PC_DIM_TH / 2.0) + 1);
		
		Posecells[x_pc][y_pc][th_pc] = 1;
		int[] max_act_xyth_path = {x_pc, y_pc, th_pc};

		// Set the initial position in the odo and experience map
		double[] odo = {0, 0, (PI / 2)};
		
		// Specify movie and frames to read
		// In our case, specify image size, in x and y direction
		
		// store size in a variable
		// 5 used as random size
		VT[] vt = new VT[5];
		int numvts = 1;
		vt[numvts].template_decay = 1.0;
		// Need to fix parameters; more specifically, array sizes
		vt[1] = new VT(numvts, new int[][]{},1.0,x_pc,y_pc,th_pc,1,1,new int[5]);
		
		Experience[] exps = new Experience[5];
		exps[1] = new Experience(x_pc, y_pc, th_pc, 0, 0, (PI/2), 1, 0, new int[5]);
		
		// Process the parameters
			//  nargin: number of arguments passed to main
			// varargin: 1xN cell array, in which N is the number of inputs that the 
				// function receives after explicitly declared inputs; basica
		for (int i = 0; i < (args.length - 3); i++)
		{
			
		}
	}

	
	
	

}
