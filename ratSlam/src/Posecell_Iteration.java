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
	double PC_E_XY_WRAP;
	double PC_E_TH_WRAP;
	double PC_I_XY_WRAP;
	double PC_I_TH_WRAP;
	Posecells PC_W_EXCITE = new Posecells(PC_W_E_DIM, PC_W_E_VAR);
	Posecells PC_W_INHIB = new Posecells(PC_W_I_DIM, PC_W_I_VAR);
	double PC_VT_INJECT_ENERGY;
	int act_x;
	int act_y;
	int act_th;
	double energy;
	double[][][] pca_new;
	double dir;
	
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
						//pca_new
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
						//pca_new
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
				
			}
			else if (dir == PI / 2)
			{
				
			}
			else if (dir == PI)
			{
				
			}
			else if (dir == (3 * PI) / 2)
			{
				
			}
			else
			{
				// rotate  poseCells instead of implementing for four quadrants
				// Do stuff here
				
				// extend poseCells one unit in each direction 
			}
		}
	}
}
