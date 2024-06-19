if (Object.mergeDeep === undefined) {
	Object.mergeDeep = (...objects: any[]) => {
		const isObject = (obj: any) => obj && typeof obj == 'object';

		if (Array.isArray(objects[0]) || Array.isArray(objects[1])) {
			return objects.reduce((prev, obj) => {
				if (obj === undefined) return prev;
				return prev.concat(...obj);
			}, []);
		}

		return objects.reduce((prev, obj) => {
			if (obj === undefined) return prev;
			Object.keys(obj).forEach((key) => {
				const pVal = prev[key];
				const oVal = obj[key];

				if (Array.isArray(pVal) && Array.isArray(oVal)) {
					prev[key] = pVal.concat(...oVal);
				} else if (isObject(pVal) && isObject(oVal)) {
					prev[key] = Object.mergeDeep(pVal, oVal);
				} else {
					prev[key] = oVal;
				}
			});

			return prev;
		}, {});
	};
}
