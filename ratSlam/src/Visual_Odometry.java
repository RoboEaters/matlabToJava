// if we already have odometry, I don't think this will be necessary
public class Visual_Odometry 
{
	static final double PI = Math.PI;

	int IMAGE_Y_SIZE = 100;		
	int IMAGE_X_SIZE = 100;	
	
	// Set up the visual odometry
	double vrot = 0;
	double vtrans = 0;
	int[] IMAGE_ODO_X_RANGE = setRange(IMAGE_X_SIZE);
	int[] IMAGE_VTRANS_Y_RANGE = setRange(IMAGE_Y_SIZE);
	int[] IMAGE_VROT_Y_RANGE = setRange(IMAGE_Y_SIZE);
	int VTRANS_SCALE = 100;
	int VISUAL_ODO_SHIFT_MATCH = 140;

	int[] prev_vrot_image_X_sums;
	int[] prev_vtrans_image_x_sums;

	int accum_delta_x = 0;
	int accum_delta_y = 0;
	double accum_delta_facing = PI / 2;

	int numexps = 1;
	int curr_exp_id = 1;
	int exp_history = 1;
	double EXP_CORRECTION = 0.5;
	double EXP_LOOPS = 100;
	double EXP_DELTA_PC_THRESHOLD = 1.0;

	int ODO_ROT_SCALING = 1;
	int ODO_VTRANS_SCALING = 1;
	int POSECELL_VTRANS_SCALING = 1;
	int ODO_FILE = 1;

	public void visual_odometry(int raw_image[][])
	{
		int FOV_DEG = 50;
		int dpp = FOV_DEG / raw_image[1].length;

		// vtrans
		int sub_image[][]= {IMAGE_VTRANS_Y_RANGE,IMAGE_ODO_X_RANGE};

		int[] image_x_sums = {};
		for (int i = 0; i < sub_image.length; i++)
		{
			for(int j = 0; j < sub_image[1].length; i++)
			{
				image_x_sums[i] = sub_image[j][i];
			}
		}

		int avintNum = 0;
		for (int x = 0; x < image_x_sums.length; x++)
		{
			avintNum += image_x_sums[x];
		}
		int avint = avintNum / image_x_sums.length;

		for (int y = 0; y < image_x_sums.length; y++)
		{
			image_x_sums[y] = image_x_sums[y] / avint;
		}

		// Change/ translate :(
		int mindiff = 0;

		int vtrans = mindiff * VTRANS_SCALE;

		// a hack to detect excessively large vtrans
		if (vtrans > 10)
		{
			vtrans = 0;
		}

		prev_vtrans_image_x_sums = image_x_sums;

		// now do rotations
		// v- change image_vrot_y_range and image_Odo_x_range <- reinitialize
		int[][] sub_image2 = {IMAGE_VROT_Y_RANGE, IMAGE_ODO_X_RANGE};

		//image_x_sums = null;
		for (int z = 0; z < sub_image2[1].length; z++)
		{
			for (int z2 = 0; z2 < sub_image2[0].length; z2++)
			{
				image_x_sums[z] = sub_image2[z2][z];
			}
		}
		int avintSum = 0;
		for (int i = 0; i < image_x_sums.length; i++)
		{
			avintSum += image_x_sums[i];
		}
		avint = avintSum / image_x_sums.length;

		for (int j = 0; j < image_x_sums.length; j++)
		{
			image_x_sums[j] = image_x_sums[j] / avint;
		} 

		// minoffset, mindiff stuff
		int minoffset = 0;
		vrot = minoffset *dpp * PI / 180;
		prev_vrot_image_X_sums = image_x_sums;
	}
	
	private int[] setRange(int range)
	{
		int[] toReturn = new int[range];
		for (int i = 0; i < range; i++)
		{
			toReturn[i] = i;
		}
		return toReturn;
	}
}
