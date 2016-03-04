package com.sqsmv.productloc.database.product;

import com.sqsmv.productloc.database.XMLDBContract;

public class ProductContract implements XMLDBContract
{
    protected static final String TABLE_NAME = "Product";
    protected static final String XML_FILE_NAME = "product2.xml";
    public static final String COLUMN_NAME_MASNUM = "masnum";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_CATEGORY = "category";
    public static final String COLUMN_NAME_RATING = "rating";
    public static final String COLUMN_NAME_STREETDATE = "streetdate";
    public static final String COLUMN_NAME_TITLEFILM = "titlefilm";
    public static final String COLUMN_NAME_NOCOVER = "nocover";
    public static final String COLUMN_NAME_PRICELIST = "pricelist";
    public static final String COLUMN_NAME_ISNEW = "isnew";
    public static final String COLUMN_NAME_ISBOXSET = "isboxset";
    public static final String COLUMN_NAME_MULTIPACK = "multipack";
    public static final String COLUMN_NAME_MEDIAFORMAT = "mediaformat";
    public static final String COLUMN_NAME_PRICEFILTERS = "pricefilters";
    public static final String COLUMN_NAME_SPECIALFIELDS = "specialfields";
    public static final String COLUMN_NAME_STUDIO = "studio";
    public static final String COLUMN_NAME_SEASON = "season";
    public static final String COLUMN_NAME_NUMBEROFDISCS = "numberofdiscs";
    public static final String COLUMN_NAME_THEATERDATE = "theaterdate";
    public static final String COLUMN_NAME_STUDIONAME = "studioname";

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    @Override
    public String getTableCreateString()
    {
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                COLUMN_NAME_MASNUM + " TEXT PRIMARY KEY, " +
                COLUMN_NAME_NAME + " TEXT, " +
                COLUMN_NAME_CATEGORY + " TEXT, " +
                COLUMN_NAME_RATING + " TEXT, " +
                COLUMN_NAME_STREETDATE + " TEXT, " +
                COLUMN_NAME_TITLEFILM + " TEXT, " +
                COLUMN_NAME_NOCOVER + " TEXT, " +
                COLUMN_NAME_PRICELIST + " TEXT, " +
                COLUMN_NAME_ISNEW + " TEXT, " +
                COLUMN_NAME_ISBOXSET + " TEXT, " +
                COLUMN_NAME_MULTIPACK + " TEXT, " +
                COLUMN_NAME_MEDIAFORMAT + " TEXT, " +
                COLUMN_NAME_PRICEFILTERS + " TEXT, " +
                COLUMN_NAME_SPECIALFIELDS + " TEXT, " +
                COLUMN_NAME_STUDIO + " TEXT, " +
                COLUMN_NAME_SEASON + " TEXT, " +
                COLUMN_NAME_NUMBEROFDISCS + " TEXT, " +
                COLUMN_NAME_THEATERDATE + " TEXT, " +
                COLUMN_NAME_STUDIONAME + " TEXT, " +
                COLUMN_NAME_SHA + " TEXT);";
    }

    @Override
    public String getTableDropString()
    {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    @Override
    public String getPrimaryKeyName()
    {
        return COLUMN_NAME_MASNUM;
    }

    @Override
    public String[] getColumnNames()
    {
        return new String[] {
                COLUMN_NAME_MASNUM,
                COLUMN_NAME_NAME,
                COLUMN_NAME_CATEGORY,
                COLUMN_NAME_RATING,
                COLUMN_NAME_STREETDATE,
                COLUMN_NAME_TITLEFILM,
                COLUMN_NAME_NOCOVER,
                COLUMN_NAME_PRICELIST,
                COLUMN_NAME_ISNEW,
                COLUMN_NAME_ISBOXSET,
                COLUMN_NAME_MULTIPACK,
                COLUMN_NAME_MEDIAFORMAT,
                COLUMN_NAME_PRICEFILTERS,
                COLUMN_NAME_SPECIALFIELDS,
                COLUMN_NAME_STUDIO,
                COLUMN_NAME_SEASON,
                COLUMN_NAME_NUMBEROFDISCS,
                COLUMN_NAME_THEATERDATE,
                COLUMN_NAME_STUDIONAME,
                COLUMN_NAME_SHA };
    }

    @Override
    public String getXMLFileName()
    {
        return XML_FILE_NAME;
    }
}
