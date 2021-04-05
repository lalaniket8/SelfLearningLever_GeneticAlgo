/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.collision;

/**
 * A {@link Filter} for categorized fixtures.
 * <p>
 * Constructing a {@link CategoryFilter} requires supplying
 * two integers: a category integer and a mask integer.
 * <p>
 * The usage of this class can be explained as follows:
 * <pre> CategoryFilter f1 = new CategoryFilter(1, 1);
 * CategoryFilter f2 = new CategoryFilter(2, 2);
 * CategoryFilter f3 = new CategoryFitler(3, 3);
 * 
 * f1.isAllowed(f2); // returns false
 * f2.isAllowed(f3); // returns true?!</pre>
 * This is because the integers passed in are being used via their binary representation.
 * If we examine the binary representation of these numbers:
 * <pre> 1 = 0...0001
 * 2 = 0...0010
 * 3 = 0...0011</pre>
 * We see that 3 is actually a combination of 1 and 2.  Because of this <code>f3</code>
 * will actually be part of category 1 and 2, not its own category.
 * <p>
 * Because of this representation, there are a maximum of 64 categories that can be
 * represented:
 * <pre> category  1 =  1 = 2^0
 * category  2 =  2 = 2^1
 * category  3 =  4 = 2^2
 * category  4 =  8 = 2^3
 * category  5 = 16 = 2^4
 * ...
 * category 64 = Long.MAX_VALUE = 2^64</pre>
 * In addition, the mask integer is handled in a similar way.  <code>f3</code> will be able to
 * collide with both category 1 and 2 because of the binary representation of 3.
 * <p>
 * In general, the mask or category can be generated by OR-ing the categories.
 * For example:
 * <pre> final long CATEGORY_0 = 1;
 * final long CATEGORY_1 = 2;
 * final long CATEGORY_2 = 4;
 * final long MASK_ALL = Long.MAX_VALUE;
 * 
 * // make f1 part of category 0, and allow collisions with category 0 and 1
 * CategoryFilter f1 = new CategoryFilter(CATEGORY_0, CATEGORY_0 | CATEGORY_1);
 * 
 * // make f2 part of category 1 and 2, and allow collisions with category 0 and 2
 * CategoryFilter f2 = new CategoryFilter(CATEGORY_1 | CATEGORY_2, CATEGORY_0 | CATEGORY_2);
 * 
 * // make f3 part of category 2, and allow collision with every category
 * CategoryFilter f3 = new CategoryFilter(CATEGORY_2, MASK_ALL);
 * 
 * f1.isAllowed(f2); // returns true since f1 can collide with 0 or 1 and f2 is part of 1
 *                   // and because f2 can collide with 0 or 2 and f1 is part of 0
 *                   
 * f1.isAllowed(f3); // returns false since f1 can collide with 0 or 1 but f3 is part of 2 only
 *                   // even though f3 is allowed to collide with all categories (both must work)
 *                   
 * f2.isAllowed(f3); // returns true since f2 can collide with 0 or 2 and f3 is part of 2
 *                   // and because f3 can collide with all categories</pre>
 * As its apparent from the code above, both f1.isAllowed(f2) and f2.isAllowed(f1) must return true
 * if the entire result is to be deemed true.
 * <p>
 * By default the {@link CategoryFilter} will be set to category 1 and
 * have a mask of all category bits.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public final class CategoryFilter implements Filter {
	/** The category this object is in */
	protected final long category;
	
	/** The categories this object can collide with */
	protected final long mask;
	
	/**
	 * Default constructor.
	 * <p>
	 * By default the category is 1 and the mask is all categories.
	 */
	public CategoryFilter() {
		this.category = 1;
		this.mask = Long.MAX_VALUE;
	}
	
	/**
	 * Full constructor.
	 * @param category the category bits
	 * @param mask the mask bits
	 */
	public CategoryFilter(long category, long mask) {
		super();
		this.category = category;
		this.mask = mask;
	}
	
	/**
	 * Returns true if the given {@link Filter} and this {@link Filter}
	 * allow the objects to interact.
	 * <p>
	 * If the given {@link Filter} is not the same type as this {@link Filter}
	 * then a value of true is returned.
	 * <p>
	 * If the given {@link Filter} is null, a value of true is returned.
	 * @param filter the other {@link Filter}
	 * @return boolean
	 */
	@Override
	public boolean isAllowed(Filter filter) {
		// make sure the given filter is not null
		if (filter == null) return true;
		// check the type
		if (filter instanceof CategoryFilter) {
			// cast the filter
			CategoryFilter cf = (CategoryFilter) filter;
			// perform the check
			return (this.category & cf.mask) > 0 && (cf.category & this.mask) > 0;
		}
		// if its not of right type always return true
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof CategoryFilter) {
			CategoryFilter filter = (CategoryFilter)obj;
			return filter.category == this.category && filter.mask == this.mask;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + (int)((this.category >>> 32) ^ this.category);
		hash = hash * 31 + (int)((this.mask >>> 32) ^ this.mask);
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CategoryFilter[Category=").append(this.category)
		.append("|Mask=").append(this.mask)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the category bits.
	 * @return long the category bits
	 */
	public long getCategory() {
		return this.category;
	}
	
	/**
	 * Returns the mask bits.
	 * @return long the mask bits
	 */
	public long getMask() {
		return this.mask;
	}
}
