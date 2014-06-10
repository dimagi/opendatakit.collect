package org.odk.collect.android.views.media;

public class ViewId {
	
	private long rowId;
	private long colId;
	
	public ViewId(long a, long b) {
		rowId = a;
		colId = b;
	}
	
	public long getRow() {
		return rowId;
	}
	
	public long getCol() {
		return colId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (colId ^ (colId >>> 32));
		result = prime * result + (int) (rowId ^ (rowId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViewId other = (ViewId) obj;
		return colId == other.getCol() && rowId == other.getRow();
	}

}
