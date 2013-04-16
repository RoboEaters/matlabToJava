// rs_posecell_iteration (vt_id, vtrans, vrot)
public class Posecell_Iteration 
{
	static final double PI = Math.PI;

	// Constants Needed
	static int PC_DIM_XY = 61;
	static int PC_DIM_TH = 36;
	static int PC_W_E_DIM = 7;
	static int PC_W_I_DIM = 5;
	static int PC_W_E_VAR = 1;
	static int PC_W_I_VAR = 2;
	static double PC_GLOBAL_INHIB = 0.00002;

	// Variables Needed
	double PC_C_SIZE_TH = (2 * PI) / PC_DIM_TH;
	int[] PC_E_XY_WRAP;
	int[] PC_E_TH_WRAP;
	int[] PC_I_XY_WRAP;
	int[] PC_I_TH_WRAP;
	Posecells PC_W_EXCITE = new Posecells(PC_W_E_DIM, PC_W_E_VAR);
	Posecells PC_W_INHIB = new Posecells(PC_W_I_DIM, PC_W_I_VAR);
	double PC_VT_INJECT_ENERGY;
	int act_x;
	int act_y;
	int act_th;
	double energy;
	double[][][] pca_new;
	double dir;

	// My variables
	double[][][] pca90;
	double dir90;

	// Arguments in Constructor
	int vt_id;
	int vtrans;
	int vrot;
	Posecells poseCells;
	VT[] vt;

	public Posecell_Iteration(int vtId, int trans, int rot, Posecells p, VT[] v)
	{
		vt_id = vtId;
		vtrans = trans;
		vrot = rot;
		poseCells = p;
		vt = v;
	}

	public void iteration()
	{
		// if this isn't a new vt, then add the energy at its associated posecell location
		if (vt[vt_id].first != 1)
		{
			act_x = Math.min(Math.max(Math.round(vt[vt_id].x_pc), 1), PC_DIM_XY);
			act_y = Math.min(Math.max(Math.round(vt[vt_id].y_pc), 1), PC_DIM_XY);
			act_th = Math.min(Math.max(Math.round(vt[vt_id].th_pc), 1), PC_DIM_TH);

			// This decays the amount of energy that's injected at the vt's posecell location
			// This is important as the Posecells poseCells will erroneously snap
			// for bad vt matches that occur over long periods (ex. a bad match that occurs while agent is stationary)
			// This means that multiple vt's need to be recognized for a snap to happen
			energy = PC_VT_INJECT_ENERGY * (1/30) * (30 - Math.exp(1.2 * vt[vt_id].template_decay));
			if (energy > 0)
			{
				poseCells.weights[act_x][act_y][act_th] += energy;
			}
		}

		// local excitation - PC_le = PC elements + PC weights
		pca_new = new double[PC_DIM_XY][PC_DIM_XY][PC_DIM_TH];
		for (int i = 0; i < PC_DIM_XY; i++)
		{
			for (int j = 0; j < PC_DIM_XY; j++)
			{
				for (int k = 0; k < PC_DIM_TH; k++)
				{
					if (poseCells.weights[i][j][k] != 0)
					{
						for (int m = 0; m < (m + PC_W_E_DIM - 1); m++)
							for (int n = 0; n < (n + PC_W_E_DIM - 1); n++)
								for (int o = 0; o < (o + PC_W_E_DIM - 1); o++)
									pca_new[PC_E_XY_WRAP[m]][PC_E_XY_WRAP[n]]
											[PC_E_TH_WRAP[o]] = pca_new[PC_E_XY_WRAP[m]]
													[PC_E_XY_WRAP[n]][PC_E_TH_WRAP[o]] 
															* PC_W_EXCITE.weights[i][j][k];					
					}
				}
			}
		}
		poseCells.weights = pca_new;

		// local inhibition - PC_li = PC_;e - PC_le elements * PC weights

		// re-initialize pca_new
		pca_new = new double[PC_DIM_XY][PC_DIM_XY][PC_DIM_TH];
		for (int i = 0; i < PC_DIM_XY; i++)
		{
			for (int j = 0; j < PC_DIM_XY; j++)
			{
				for (int k = 0; k < PC_DIM_TH; k++)
				{
					if (poseCells.weights[i][j][k] != 0)
					{
						for (int m = 0; m < (m + PC_W_I_DIM - 1); m++)
							for (int n = 0; n < (n + PC_W_I_DIM - 1); n++)
								for (int o = 0; o < (o + PC_W_I_DIM - 1); o++)
									pca_new[PC_I_XY_WRAP[m]][PC_I_XY_WRAP[n]]
											[PC_I_TH_WRAP[o]] = pca_new[PC_I_XY_WRAP[m]]
													[PC_I_XY_WRAP[n]][PC_I_TH_WRAP[o]] 
															* PC_W_INHIB.weights[i][j][k];
					}
				}
			}
		}


		// Subtract every posecell by pca_new
		for (int i = 0; i < PC_DIM_XY; i++)
		{
			for (int j = 0; j < PC_DIM_XY; j++)
			{
				for (int k = 0; k < PC_DIM_TH; k++)
				{
					poseCells.weights[i][j][k] -= pca_new[i][j][k];
				}
			}
		}

		// Set poseCells again taking inhibition into account
		// local global inhibition - PC_gi = PC_li elements - inhibition
		// Also, find total after inhibition is taken into account and divide each poseCells element by total
		double total = 0;
		for (int i = 0; i < PC_DIM_XY; i++)
		{
			for (int j = 0; j < PC_DIM_XY; j++)
			{
				for (int k = 0; k < PC_DIM_TH; k++)
				{
					double x = poseCells.weights[i][j][k];
					if (x >= PC_GLOBAL_INHIB)
					{
						poseCells.weights[i][j][k] *= (x - PC_GLOBAL_INHIB);
						total += poseCells.weights[i][j][k];
					}
				}
			}
		}

		// Divide every element of poseCells by total
		for (int i = 0; i < PC_DIM_XY; i++)
		{
			for (int j = 0; j < PC_DIM_XY; j++)
			{
				for (int k = 0; k < PC_DIM_TH; k++)
				{
					double x = poseCells.weights[i][j][k];					
					poseCells.weights[i][j][k] = x / total;
				}
			}
		}

		// Path Integration
		// vtrans affects xy directions
		// shift in each th given by the th
		for (int dir_pc = 0; dir_pc < PC_C_SIZE_TH; dir_pc++)
		{
			// radians
			dir = (dir_pc - 1) * PC_C_SIZE_TH;

			// north, east, south, west are straightforward
			if (dir == 0)
			{
				int[] dirArray = {0, 1};
				goThrough(dir_pc, dirArray);
			}
			else if (dir == PI / 2)
			{
				int[] dirArray = {1, 0};
				goThrough(dir_pc, dirArray);
			}
			else if (dir == PI)
			{
				int[] dirArray = {0, -1};
				goThrough(dir_pc, dirArray);
			}
			else if (dir == (3 * PI) / 2)
			{
				int[] dirArray = {-1, 0};
				goThrough(dir_pc, dirArray);
			}
			else
			{
				// rotate  poseCells instead of implementing for four quadrants
				pca90 = rot90(poseCells.weights, 0, (int)Math.floor(dir * 2 / PI));
				dir90 = dir - Math.floor(dir * 2 / PI) * (PI / 2);

				pca_new = new double[PC_DIM_XY + 2][][];

				// Remaining commands in else statement go below
				for(int i = 1; i < pca_new.length-1; i++)
				{
					for(int j = 2; j < pca_new.length - 1; j++)
					{
						// is pca_new a 2-d or 3-d array
						pca_new[i][j] = pca90[i][j];
					}
				}
				double weight_sw = Math.pow(vtrans, 2) * Math.cos(dir90) * Math.sin(dir90);
				double weight_se = vtrans * Math.sin(dir90) 
						- (vtrans*vtrans*Math.cos(dir90)*Math.sin(dir90));
				double weight_nw = vtrans * Math.cos(dir90) 
						- (vtrans*vtrans*Math.cos(dir90)*Math.sin(dir90));
				double weight_ne = 1.0 - weight_sw - weight_se - weight_nw;	

				// circular shift and multiple by the contributing weight
				// copy those shifted elements for the wrap around
				for (int i = 0; i < pca_new[1].length; i++)
				{
					for (int j = 0; j < pca_new[2].length; j++)
					{
						for (int k = 0; k < pca_new[3].length; k++)
						{
							double[][][] addend1 = circshift(pca_new, new int[]{0, 1});
							double[][][] addend2 = circshift(pca_new, new int[]{1, 0});
							double[][][] addend3 = circshift(pca_new, new int[]{1, 1});
							pca_new[i][k][j] = pca_new[i][j][k]*weight_ne + 
									multiply_elements(addend1, weight_nw)[i][j][k] +
									multiply_elements(addend2, weight_se)[i][j][k] +
									multiply_elements(addend3, weight_sw)[i][j][k];
						}
					}
				}

				// pca90 = pca_new(2:end-1, 2:end-1);
				for (int i = 1; i < pca_new[1].length - 1; i++)
				{
					for (int j = 1; j < pca_new[2].length - 1; j++)
					{
						pca90[i-1][j-1] = pca_new[i][j];
					}
				}

				// pca90(2:end, 1) = pca90(2:end, 1) + pca_new(3:end-1, end);
				for (int i = 1; i < pca90.length; i++)
				{
					for (int j = 0; j < pca90[2].length; j++)
					{
						pca90[i][j][0] = pca90[i][j][0] + pca_new[i+1][j][0];
					}
				}

				// pca90(1, 2:end) = pca90(1, 2:end) + pca_new(end, 3:end-1);
				for (int i = 1; i < pca90.length; i++)
				{
					for (int j = pca90[2].length-1; j >= 0; j--)
					{
						pca90[i][j][0] = pca90[i][j][0] + pca_new[i][pca90[2].length - j][0];
					}
				}

				// pca(1,1) = pca90(1,1) + pca_new(end, end);
				pca90[1][1][0] = pca90[1][1][0] + pca_new[pca_new.length - 1][pca_new.length - 1][0];

				// unrotate the pose cell xy layer
				for (int i = 0; i < poseCells.weights[1].length; i++)
				{
					for (int j = 0; j < poseCells.weights[2].length; j++)
					{
						poseCells.weights[i][j][dir_pc] = rot90(pca90, 0, (int)(4 - Math.floor(dir * 2 / PI)))[i][j][dir_pc];
					}
				}
			}
		}

		// Path Integration - Theta
		// Shift the pose cells +/- theta given by vrot
		if (vrot != 0)
		{
			// mod to work out the partial shift amount
			double weight = (Math.abs(vrot) / PC_C_SIZE_TH) % 1;
			if (weight == 0)
			{
				weight = 1.0;
			}
			// THIS!!! need to do sign method - Use Math.signum(x) function
			//poseCells.weights = circshift(poseCells.weights, new int[]{0, 0, Math.si)ift(poseCells.weights, [0 0 sign(vrot) * Math.floor(Math.abs(vrot))/PC_C_SIZE_TH] * (1.0 - weight)
				//	+ circshift(poseCells.weight, [0 0 sign(vrot) * Math.ceil(Math.abs(vrot)/PC_C_SIZE_TH)]) * (weight); 
			}
		}

		// Use when doing N, E, S, W directions
		private void goThrough (int constThird, int[] direct)
		{
			for (int i = 0; i < PC_DIM_XY; i++)
			{
				for (int j = 0; j < PC_DIM_XY; j++)
				{
					double toUse = poseCells.weights[i][j][constThird];			
					poseCells.weights[i][j][constThird] = (toUse * (1 - vtrans)) + (
							multiply_elements(circshift(poseCells.weights, direct), vtrans)[i][j][constThird]);
				}
			}
		}

		// Think about having temp storage for poseCells.weights that use circshift, then have poseCells.weights equal temp storage
		private double[][][] circshift(double[][][] check, int[] dir_Use)
		{
			if (dir_Use[1] == 1)
			{
				shiftDown(check, vt_id);
				if (dir_Use[2] == 1)
				{	
					shiftLeft(check, vt_id);
				}
			}
			else if (dir_Use[1] == 0)
			{
				if (dir_Use[2] == 1)
				{
					shiftLeft(check, vt_id);
				}
			}
			return null;
		}

		// B = rot90(A,k) rotates matrix A counterclockwise by k*90 degrees, where k is an integer.
		private double[][][] rot90(double[][][] use, int constant, int numRotations)
		{
			double[][][] toReturn = use;
			int size = toReturn.length;
			int size2 = toReturn[2].length;
			for (int num = 0; num < numRotations; num++)
			{
				for(int i=0; i<size; i++)
				{
					for(int j=0; j<size2; j++)
					{
						toReturn[i][j][constant] = use[j][size-i-1][constant];
					}
				}
			}
			return toReturn;
		}

		private double[][][]multiply_elements(double[][][] toChange, double toMultiply)
		{

			return toChange;
		}

		private double[][][] shiftDown(double[][][] toReturn, int constant)
		{
			double[][][] temp = toReturn;
			for (int i = 0; i < toReturn[1].length - 1; i++)
			{
				for (int j = 0; j < toReturn[2].length - 1; j++)
				{
					if (++i == toReturn[1].length)
					{
						toReturn[1][j][constant] = temp[i][j][constant];
					}
					else
					{
						toReturn[i+1][j][constant] = temp[i][j][constant];
					}
				}
			}
			return toReturn;
		}

		private double[][][] shiftLeft(double[][][] toReturn, int constant)
		{
			double[][][] temp = toReturn;
			for (int i = 0; i < toReturn[1].length - 1; i++)
			{
				for (int j = 0; j < toReturn[2].length - 1; j++)
				{
					if (++j == toReturn[1].length)
					{
						toReturn[i][1][constant] = temp[i][j][constant];
					}
					else
					{
						toReturn[i][j+1][constant] = temp[i][j][constant];
					}
				}
			}
			return toReturn;
		}
	}
