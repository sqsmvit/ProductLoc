package com.sqsmv.productloc.database.product;

import android.database.Cursor;

import com.sqsmv.productloc.database.XMLDBRecord;

public class ProductRecord extends XMLDBRecord
{
    private String masNum;
    private String name;
    private String category;
    private String rating;
    private String streetDate;
    private int titleFilm;
    private int noCover;
    private String priceList;
    private int isNew;
    private int isBoxSet;
    private int multipack;
    private String mediaFormat;
    private String priceFilters;
    private String specialFields;
    private String studio;
    private String season;
    private int numberOfDiscs;
    private String theaterDate;
    private String studioName;

    public ProductRecord()
    {
        super(new ProductContract());
        masNum = "";
        name = "***ERR: NOT FOUND***";
        category = "";
        rating = "";
        streetDate = "";
        titleFilm = -1;
        noCover = -1;
        priceList = "";
        isNew = -1;
        isBoxSet = -1;
        multipack = -1;
        mediaFormat = "";
        priceFilters = "";
        specialFields = "";
        studio = "";
        season = "";
        numberOfDiscs = -1;
        theaterDate = "";
        studioName = "";
        setSha("");
    }

    public ProductRecord(String masNum, String name, String category, String rating,
                         String streetDate, int titleFilm, int noCover, String priceList,
                         int isNew, int isBoxSet, int multipack, String mediaFormat,
                         String priceFilters, String specialFields, String studio, String season,
                         int numberOfDiscs, String theaterDate, String studioName, String sha)
    {
        super(new ProductContract());
        this.masNum = masNum;
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.streetDate = streetDate;
        this.titleFilm = titleFilm;
        this.noCover = noCover;
        this.priceList = priceList;
        this.isNew = isNew;
        this.isBoxSet = isBoxSet;
        this.multipack = multipack;
        this.mediaFormat = mediaFormat;
        this.priceFilters = priceFilters;
        this.specialFields = specialFields;
        this.studio = studio;
        this.season = season;
        this.numberOfDiscs = numberOfDiscs;
        this.theaterDate = theaterDate;
        this.studioName = studioName;
        setSha(sha);
    }

    public ProductRecord(Cursor dbCursor)
    {
        super(new ProductContract());
        masNum = "";
        name = "***ERR: NOT FOUND***";
        category = "";
        rating = "";
        streetDate = "";
        titleFilm = -1;
        noCover = -1;
        priceList = "";
        isNew = -1;
        isBoxSet = -1;
        multipack = -1;
        mediaFormat = "";
        priceFilters = "";
        specialFields = "";
        studio = "";
        season = "";
        numberOfDiscs = -1;
        theaterDate = "";
        studioName = "";
        setSha("");
        if(dbCursor.moveToFirst())
        {
            buildWithCursor(dbCursor);
            dbCursor.close();
        }
    }

    public String getMasNum()
    {
        return masNum;
    }

    public String getName()
    {
        return name;
    }

    public String getCategory()
    {
        return category;
    }

    public String getRating()
    {
        return rating;
    }

    public String getStreetDate()
    {
        return streetDate;
    }

    public int getTitleFilm()
    {
        return titleFilm;
    }

    public int getNoCover()
    {
        return noCover;
    }

    public String getPriceList()
    {
        return priceList;
    }

    public int getIsNew()
    {
        return isNew;
    }

    public int getIsBoxSet()
    {
        return isBoxSet;
    }

    public int getMultipack()
    {
        return multipack;
    }

    public String getMediaFormat()
    {
        return mediaFormat;
    }

    public String getPriceFilters()
    {
        return priceFilters;
    }

    public String getSpecialFields()
    {
        return specialFields;
    }

    public String getStudio()
    {
        return studio;
    }

    public String getSeason()
    {
        return season;
    }

    public int getNumberOfDiscs()
    {
        return numberOfDiscs;
    }

    public String getTheaterDate()
    {
        return theaterDate;
    }

    public String getStudioName()
    {
        return studioName;
    }

    public void setMasNum(String masNum)
    {
        this.masNum = masNum;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public void setRating(String rating)
    {
        this.rating = rating;
    }

    public void setStreetDate(String streetDate)
    {
        this.streetDate = streetDate;
    }

    public void setTitleFilm(int titleFilm)
    {
        this.titleFilm = titleFilm;
    }

    public void setNoCover(int noCover)
    {
        this.noCover = noCover;
    }

    public void setPriceList(String priceList)
    {
        this.priceList = priceList;
    }

    public void setIsNew(int isNew)
    {
        this.isNew = isNew;
    }

    public void setIsBoxSet(int isBoxSet)
    {
        this.isBoxSet = isBoxSet;
    }

    public void setMultipack(int multipack)
    {
        this.multipack = multipack;
    }

    public void setMediaFormat(String mediaFormat)
    {
        this.mediaFormat = mediaFormat;
    }

    public void setPriceFilters(String priceFilters)
    {
        this.priceFilters = priceFilters;
    }

    public void setSpecialFields(String specialFields)
    {
        this.specialFields = specialFields;
    }

    public void setStudio(String studio)
    {
        this.studio = studio;
    }

    public void setSeason(String season)
    {
        this.season = season;
    }

    public void setNumberOfDiscs(int numberOfDiscs)
    {
        this.numberOfDiscs = numberOfDiscs;
    }

    public void setTheaterDate(String theaterDate)
    {
        this.theaterDate = theaterDate;
    }

    public void setStudioName(String studioName)
    {
        this.studioName = studioName;
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
            getMasNum(), getName(), getCategory(), getRating(), getStreetDate(), Integer.toString(getTitleFilm()),
            Integer.toString(getNoCover()), getPriceList(), Integer.toString(getIsNew()), Integer.toString(getIsBoxSet()),
            Integer.toString(getMultipack()), getMediaFormat(), getPriceFilters(), getSpecialFields(), getStudio(), getSeason(),
            Integer.toString(getNumberOfDiscs()), getTheaterDate(), getStudioName(), getSha()
        };
    }

    @Override
    public void setFromCursor(Cursor dbCursor)
    {
        for(int count = 0; count < dbCursor.getColumnCount(); count++)
        {
            if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_MASNUM))
            {
                setMasNum(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_NAME))
            {
                setName(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_CATEGORY))
            {
                setCategory(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_RATING))
            {
                setRating(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_STREETDATE))
            {
                setStreetDate(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_TITLEFILM))
            {
                setTitleFilm(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_NOCOVER))
            {
                setNoCover(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_PRICELIST))
            {
                setPriceList(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_ISNEW))
            {
                setIsNew(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_ISBOXSET))
            {
                setIsBoxSet(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_MULTIPACK))
            {
                setMultipack(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_MEDIAFORMAT))
            {
                setMediaFormat(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_PRICEFILTERS))
            {
                setPriceFilters(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_SPECIALFIELDS))
            {
                setSpecialFields(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_STUDIO))
            {
                setStudio(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_SEASON))
            {
                setSeason(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_NUMBEROFDISCS))
            {
                setNumberOfDiscs(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_THEATERDATE))
            {
                setTheaterDate(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_STUDIONAME))
            {
                setStudioName(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProductContract.COLUMN_NAME_SHA))
            {
                setSha(dbCursor.getString(count));
            }
        }
    }
}
