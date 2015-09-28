package magma.tools.competition.util;

import java.util.Iterator;

public class ShiftableMatrix<T>
{

	private Object[][] elements;

	public ShiftableMatrix(int width, int height)
	{
		elements = new Object[width][height];
	}

	public void set(int x, int y, T element)
	{
		elements[x][y] = element;
	}

	@SuppressWarnings("unchecked")
	public T get(int x, int y)
	{
		return (T) elements[x][y];
	}

	public void putAllRowsFirst(Iterable<T> iterable)
	{
		int row = 0;
		int col = 0;
		for (T t : iterable) {
			if (row == getWidth()) {
				row = 0;
				col++;
			}
			if (col == getHeight()) {
				break;
			}
			set(row, col, t);
			row++;
		}
	}

	public void putAllColumnsFirst(Iterable<T> iterable)
	{
		int row = 0;
		int col = 0;
		for (T t : iterable) {
			if (col == getHeight()) {
				col = 0;
				row++;
			}
			if (row == getWidth()) {
				break;
			}
			set(row, col, t);
			col++;
		}
	}

	public void clear(int x, int y)
	{
		elements[x][y] = null;
	}

	public void shiftRight(int y)
	{
		Object lastElement = elements[getWidth() - 1][y];
		for (int i = getWidth() - 1; i > 0; i--) {
			elements[i][y] = elements[i - 1][y];
		}
		elements[0][y] = lastElement;
	}

	public void shiftRight(int y, int amount)
	{
		for (int i = 0; i < amount; i++) {
			shiftRight(y);
		}
	}

	public int getWidth()
	{
		return elements.length;
	}

	public int getHeight()
	{
		return elements[0].length;
	}

	public void reorder()
	{
		for (int i = 0; i < getHeight(); i++) {
			shiftRight(i, i);
		}
	}

	public Iterator<T> iteratorRowsFirst()
	{
		return new ShiftableMatrixIterator<T>(this, false);
	}

	public Iterator<T> iteratorColumnsFirst()
	{
		return new ShiftableMatrixIterator<T>(this, true);
	}

	private static class ShiftableMatrixIterator<T> implements Iterator<T>
	{

		private ShiftableMatrix<T> matrix;

		private boolean columnsFirst;

		private int xPos;

		private int yPos;

		public ShiftableMatrixIterator(ShiftableMatrix<T> matrix,
				boolean columnsFirst)
		{
			this.matrix = matrix;
			this.columnsFirst = columnsFirst;
			xPos = 0;
			yPos = 0;
		}

		@Override
		public boolean hasNext()
		{
			if (columnsFirst) {
				return (xPos < matrix.getWidth() && yPos < matrix.getHeight())
						|| (yPos == matrix.getHeight() && xPos < matrix.getWidth());
			} else {
				return (xPos < matrix.getWidth() && yPos < matrix.getHeight())
						|| (xPos == matrix.getWidth() && yPos < matrix.getHeight());
			}
		}

		@Override
		public T next()
		{
			if (hasNext()) {
				T element = matrix.get(xPos, yPos);
				inc();
				return element;
			} else {
				return null;
			}
		}

		private void inc()
		{
			if (columnsFirst) {
				yPos++;
				if (yPos == matrix.getHeight()) {
					yPos = 0;
					xPos++;
				}
			} else {
				xPos++;
				if (xPos == matrix.getWidth()) {
					xPos = 0;
					yPos++;
				}
			}
		}

	}

}
