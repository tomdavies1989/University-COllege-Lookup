package uk.ac.ucl.panda.retrieval.models;

/**
 * 
 * 
 * List of authors:
 * @author <a href="mailto:j.zhu@cs.ucl.ac.uk">Jianhan Zhu</a>
 */
public interface Distribution {

	/**
	 * Returns the distribution function <I>F</I>(<I>x</I>).
	 * 
	 * @param x value at which the distribution function is evaluated
	 * @return distribution function evaluated at <TT>x</TT>
	 */
	public double cdf(double x);

	/**
	 * Returns bar(F)(<I>x</I>) = 1 - <I>F</I>(<I>x</I>).
	 * 
	 * @param x value at which the complementary distribution function is
	 * evaluated
	 * @return complementary distribution function evaluated at <TT>x</TT>
	 */
	public double barF(double x);

	/**
	 * Returns the inverse distribution function <I>F</I><SUP>-1</SUP>
	 * (<I>u</I>), defined in.
	 * 
	 * @param u value in the interval (0, 1) for which the inverse 
	 * distribution function is evaluated
	 * @return the inverse distribution function evaluated at <TT>u</TT>
	 */
	public double inverseF(double u);

	/**
	 * Returns the mean of the distribution function.
	 */
	public double getMean();

	/**
	 * Returns the variance of the distribution function.
	 */
	public double getVariance();

	/**
	 * Returns the standard deviation of the distribution function.
	 */
	public double getStandardDeviation();

	/**
	 * Returns the parameters of the distribution function in the same order as
	 * in the constructors.
	 */
	public double[] getParams();
}
