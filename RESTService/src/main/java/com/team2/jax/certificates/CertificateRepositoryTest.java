package com.team2.jax.certificates;

public class CertificateRepositoryTest implements CertificateRepository {

	public Certificate findByUsername(String username) {
		Certificate rtn = new Certificate();
		rtn.setUsername(username);
		if(username.equals("test"))
		{
			rtn.setPublicKey("ok");
		} else if(username.equals("no"))
		{
			return null;
		}else
		{
			rtn.setPublicKey("nope");
		}
		return rtn;
	}

}
