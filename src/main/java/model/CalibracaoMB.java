package model;

//{"Token":"eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJHYXJ0ZW4gQXV0b21hw6fDo28iLCJhdWQiOiJXZWJjYWxtZWR1bWkiLCJJZF91c3VhcmlvIjoxLCJOb21lIjoiYnJlbmRvbiIsIkxvZ2luIjoiYnJlbmRvbiIsIlVzdWFyaW9BZG1pbiI6MSwiaWF0IjoxNTU4NDYxODcxLCJleHAiOjE1NTg1NDgyNzF9.rY0G2cCmg3KlsgM0GjFCnWPPfNKOsdual6UakXm2QWU","M":1,"B":0}


public class CalibracaoMB {
	private String Hash;
	private float M;
	private float B;
	
	
	public CalibracaoMB() {
	}
	
	public String getHash() {
		return Hash;
	}
	public void setHash(String Hash) {
		this.Hash = Hash;
	}
	public float  getM() {
		return M;
	}
	public void setM(float  m) {
		M = m;
	}
	public float  getB() {
		return B;
	}
	public void setB(float  b) {
		B = b;
	}

	@Override
	public String toString() {
		return "CalibracaoMB [Hash=" + Hash + ", M=" + M + ", B=" + B + "]";
	}
	
	
	
}
