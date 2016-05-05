package tu.bs.isf.featfork.exporter;

import tu.bs.isf.featfork.lib.FFDatabase;

/**
 * Created by Christopher Sontag
 */
public abstract class FFExporter {

    /**
     * Writes the database in a html file
     *
     * @param database The database instance
     * @param ratio    the ratio that should be reached
     */
    public abstract void write(FFDatabase database, double ratio);
}
