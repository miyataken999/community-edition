/*
 * Created on Mar 15, 2005
 */
package com.activiti.web.data;

import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;

/**
 * @author kevinr
 */
public interface IDataContainer
{
   /**
    * Return the currently sorted column if any
    * 
    * @return current sorted column if any
    */
   public String getCurrentSortColumn();
   
   /**
    * Returns the current sort direction. Only valid if a sort column is set.
    * True is returned for descending sort, false for accending sort.
    * 
    * @return true for descending sort, false for accending sort
    */
   public boolean getCurrentSortDirection();
   
   /**
    * Returns the current page size used for this list, or -1 for no paging.
    */
   public int getPageSize();
   
   /**
    * Return the current page the list is displaying
    * 
    * @return current page zero based index
    */
   public int getCurrentPage();
   
   /**
    * Returns true if a row of data is available
    * 
    * @return true if data is available, false otherwise
    */
   public boolean isDataAvailable();
   
   /**
    * Returns the next row of data from the data model
    * 
    * @return next row of data as a Bean object
    */
   public Object nextRow();
   
   /**
    * Sort the dataset using the specified sort parameters
    * 
    * @param column        Column to sort
    * @param bAscending    True for ascending sort, false for descending
    * @param mode          Sort mode to use (see IDataContainer constants)
    */
   public void sort(String column, boolean bAscending, String mode);
   
   public final static String SORT_CASEINSENSITIVE = "case-insensitive";
   public final static String SORT_CASESENSITIVE   = "case-sensitive";
   public final static String SORT_NUMERIC         = "numeric";
   public final static String SORT_DATE            = "date";
}
