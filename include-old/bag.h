/*
 * @(#)bag.h	1.2 98/09/15
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

#ifndef _JAVASOFT_BAG_H_
#define _JAVASOFT_BAG_H_

/* Declare general routines for manipulating a bag data structure.
 * Synchronized use is the responsibility of caller.
 */

struct bag;

/* Must be used to create a bag.  itemSize is the size
 * of the items stored in the bag. initialAllocation is a hint
 * for the initial number of items to allocate. Returns the
 * allocated bag, returns NULL if out of memory.
 */
struct bag *bagCreateBag(int itemSize, int initialAllocation);

/* Destroy the bag and reclaim the space it uses.
 */
void bagDestroyBag(struct bag *theBag);

/* Find 'key' in bag.  Assumes first entry in item is a pointer.
 * Return found item pointer, NULL if not found. 
 */
void *bagFind(struct bag *theBag, void *key);

/* Add space for an item in the bag.
 * Return allocated item pointer, NULL if no memory. 
 */
void *bagAdd(struct bag *theBag);

/* Delete specified item from bag. 
 * Does no checks.
 */
void bagDelete(struct bag *theBag, void *condemned);

/* Delete all items from the bag.
 */
void bagDeleteAll(struct bag *theBag);

/* Return the count of items stored in the bag.
 */
int bagSize(struct bag *theBag);

/* Enumerate over the items in the bag, calling 'func' for 
 * each item.  The function is passed the item and the user 
 * supplied 'arg'.  Abort the enumeration if the function
 * returns FALSE.  Return TRUE if the enumeration completed
 * successfully and FALSE if it was aborted.
 * Addition and deletion during enumeration is not supported.
 */
typedef bool_t (*bagEnumerateFunction)(void *item, void *arg);

bool_t bagEnumerateOver(struct bag *theBag, 
                        bagEnumerateFunction func, void *arg);

#endif /* !_JAVASOFT_BAG_H_ */