package at.pollaknet.api.facile.symtab.signature;

/**
 * The {@link at.pollaknet.api.facile.symtab.signature.ArrayShape} class describes the layout
 * of a general array.
 */
public class ArrayShape {

	private int rank;
	private int sizes [];
	private int lowerBounds [];
	
	ArrayShape(int rank, int [] sizes, int [] lowerBounds) {
		this.rank = rank;
		this.sizes = sizes;
		this.lowerBounds = lowerBounds;
	}
	
	/**
	 * The rank specifies the number of dimensions of an array.
	 * 
	 * @return The number of dimensions as {@code int}.
	 */
	public int getRank() {
		return rank;
	}
	
	/**
	 * The size describes the number of array elements of a dimension.
	 * 
	 * @return An array of sizes describing each dimension. Note that
	 * the number of specified dimensions can be smaller than the value
	 * of the rank (in this case there are remaining dimensions of unspecified
	 * sizes - the array always starts with the specification of the
	 * first dimension, followed by the second, the third, ...).
	 */
	public int [] getSizes() {
		return sizes;
	}
	
	/**
	 * The lower bound specifies the smallest possible index inside an array.
	 * 
	 * @return An array of {@code int} describing the lower bound of each dimension.
	 * Note that the number of specified dimensions can be smaller than the value
	 * of the rank (in this case there are remaining dimensions with unspecified
	 * lower bounds - the array always starts with the specification of the
	 * first dimension, followed by the second, the third, ...).
	 */
	public int [] getLowerBounds() {
		return lowerBounds;
	}
}
