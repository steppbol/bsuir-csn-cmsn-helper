export const UPDATE_CATALOG_SEARCH_PARAMS = 'UPDATE_CATALOG_SEARCH_PARAMS';

export function updateCatalogSearchParamsAction(name: string) {
  return {
    type: UPDATE_CATALOG_SEARCH_PARAMS,
    payload: {name}
  };
}
