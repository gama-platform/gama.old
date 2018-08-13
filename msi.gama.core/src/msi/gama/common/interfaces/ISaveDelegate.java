package msi.gama.common.interfaces;

import msi.gama.runtime.IScope;

public interface ISaveDelegate {

	boolean acceptSource(IScope scope, String extension) ;

	public String getExtension() ;

	public int save(IScope scope, Object sim, String filePath) ;
}
