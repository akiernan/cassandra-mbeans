import javax.management.remote.*
import javax.management.*
import groovy.jmx.builder.*
import groovy.json.JsonBuilder

class CassandraMBeans {
  static void main(String[] args) {

    // Setup JMX connection.
    def connection = new JmxBuilder().client(port: 7199, host: 'localhost')
    connection.connect()

    // Get the MBeanServer.
    def mbeans = connection.MBeanServerConnection

    def gmbeans = mbeans.queryNames(new ObjectName('org.apache.cassandra.metrics:*'), null).inject([]) { result, name ->
      result << new GroovyMBean(mbeans, name)
    }

    def queries = []
    gmbeans.each {
      if (['Value', 'Count'].intersect(it.listAttributeNames())) {
        def query = [ obj: it.name().canonicalName,
                      attr: it.listAttributeNames(),
                      allowDottedKeys: true,
                      useObjDomainAsKey: true,
                      outputWriters: [
                        [ settings:
                          [ typeNames: [ "name", "type", "columnfamily", "keyspace", "path", "scope"],
                            booleanAsNumber: true,
                            stringValuesAsKey: false,
                            rootPrefix: "jmx",
                            bucketType: "g",
                            host: "localhost",
                            port: 8125
                          ]
                        ]
                      ]
                    ]
        query.outputWriters[0]."@class" = "com.googlecode.jmxtrans.model.output.StatsDWriter"
        queries << query
      }
    }
    def jmxtrans = [ servers:
                     [ [ host: "localhost",
                         port: "7199",
                         alias: "cassandra",
                         queries: (queries)
                       ]
                     ]
                   ]
    println new JsonBuilder(jmxtrans).toPrettyString()
  }
}
