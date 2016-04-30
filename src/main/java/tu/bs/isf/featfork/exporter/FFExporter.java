package tu.bs.isf.featfork.exporter;

import tu.bs.isf.featfork.lib.Database;

/**
 * Created by Christopher Sontag on 30.04.2016.
 */
public abstract class FFExporter {

    public abstract void write(Database database);
}
