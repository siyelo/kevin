dataSource {
	pooled = true

	properties {
		maxActive = 50
		maxIdle = 25
		minIdle = 5
		initialSize = 5
		minEvictableIdleTimeMillis = 1800000
		timeBetweenEvictionRunsMillis = 1800000
		numTestsPerEvictionRun = 3
		maxWait = 10000
		testOnBorrow = true
		testWhileIdle = true
		testOnReturn = true
		// validationQuery = "SELECT 1"
	}
}
hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = true
	cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
	naming_strategy = org.hibernate.cfg.DefaultNamingStrategy
	//      flush.mode = 'commit'
	//      show_sql = true
}
naming_strategy = org.hibernate.cfg.DefaultNamingStrategy

// environment specific settings
environments {
	development {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'
			driverClassName = "org.hsqldb.jdbcDriver"
			url = "jdbc:hsqldb:mem:devDB"
			username = "sa";
			password = "";
		}
	}
	test {
		dataSource {
			dbCreate = "create-drop"
			driverClassName = "org.hsqldb.jdbcDriver"
			url = "jdbc:hsqldb:mem:testDb"
			username = "sa";
			password = "";

		}
		hibernate {
			cache.use_second_level_cache = false
			cache.use_query_cache = false
		}
	}
	production {
		dataSource {
			dbCreate = "update"
			driverClassName = "com.mysql.jdbc.Driver"
			// configuration defined in ${home}/.grails/kevin-config.groovy
		}
	}
}