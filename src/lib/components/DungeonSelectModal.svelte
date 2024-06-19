<script lang="ts">
	import { TabGroup, Tab, ProgressRadial, getModalStore } from '@skeletonlabs/skeleton';
	import { groups, lootTables, type Loottable } from '../';

	import { base } from '$app/paths';

	const modalStore = getModalStore();

	export let filter = '';
	export let baseValue = 0;

	const items = filter != '' ? lootTables.filter((item) => item.group === filter) : lootTables;

	export let value: {
		[key: string]: number;
	} = {};

	let flag = false;

	items.forEach(item => {
		item.contents.forEach(content => {
			if (!value[content]) {
				value[content] = baseValue;
			}
		});
	});

	const keyToGroup = (key: string) => {
		const item = lootTables.find((item) => item.contents.includes(key));
		return item!;
	};

	const getPercentage = (value: number, group: Loottable) => {
		if(group.mode == 'percentage') {
			return value / 10;
		} else {
			return Math.round((value / ((group.defaultLoottableWeight! + value) * (1 - (group.ignorePercentage ?? 0)))) * 1000) / 10;
		}
	}

	let tabSet: number = 0;

	const exit = () => {
		if ($modalStore[0]) {
			// clone value and remove each element with default value
			const parsedValue = { ...value };

			for (const [key, val] of Object.entries(parsedValue)) {
				if (val === baseValue) {
					delete parsedValue[key];
				}
			}

			$modalStore[0]!.response!(parsedValue);
			modalStore.close();
		}
	};

	const update = (e: Event) => {
		const target = e.target as HTMLInputElement;

		if (target.value === '') {
			target.value = '0';
		}

		if (+target.value < 0) {
			target.value = '0';
		}

		if (+target.value > 1000) {
			target.value = '1000';
		}

		const type = target.getAttribute('data-type');

		if (type === 'group') {
			const group = target.getAttribute('data-target') ?? '';

			const elements = items.filter((item) => group === `${item.displayName}-${item.subtitle}`)[0].contents;

			elements.forEach((item) => {
				value[item] = parseInt(target.value);
			});
		}

		flag = !flag;
	};

	const getGroupValue = (group: Loottable): string => {
		const elements = items.filter((item) =>  `${item.displayName}-${item.subtitle}` === `${group.displayName}-${group.subtitle}`)[0].contents;

		return elements.every((item) => value[item] === value[elements[0]])
			? value[elements[0]].toString()
			: '';
	};

	const getImage = async (item: string) => {
		const response = fetch(`${base}/${item}`);
		const blob = await response.then((res) => res.blob());

		const url = URL.createObjectURL(blob);

		return url;
	};
</script>

<main class="bg-surface-500 rounded-md p-2 h-[50%] overflow-y-auto w-[90%] sm:w-[80%] lg:w-[50%] m-10">
	<TabGroup>
		<Tab bind:group={tabSet} name="tab1" value={0}>Simple</Tab>
		<Tab bind:group={tabSet} name="tab2" value={1}>Advanced</Tab>
		<!-- Tab Panels --->
		<svelte:fragment slot="panel">
			{#if tabSet === 0}
				<div class="grid sm:grid-cols-4 p-2 gap-2">
					{#each items as item}
						<div class="card rounded-lg flex flex-col justify-between">
							<header class="card-header flex items-center justify-center w-full aspect-[9/10]">
								{#await getImage(item.img)}
									<ProgressRadial />
								{:then url}
									<img src={url} alt="dungeon" class="w-full" />
									<!-- small icon in corner of image -->
									<img src={groups[item.group]} alt="group" class="self-end -ml-8 w-8 h-8" />
								{/await}
							</header>
							<section class="p-4 w-full">
								<h4 class="h4 text-center w-full">{item.displayName}</h4>
								{#if item.subtitle}
									<h5 class="h6 text-sm text-center w-full">
										{item.subtitle}
									</h5>
								{/if}
								
							</section>
							<footer class="card-footer">
								<div class="input appearance-none flex items-center">
									<input
										type="number"
										class="input text-left appearance-none outline-none"
										min="-1"
										max="1000"
										data-type="group"
										data-target="{item.displayName}-{item.subtitle}"
										on:input={update}
										placeholder="-"
										value={+getGroupValue(item)}
									/>
									{#key flag}
										<span class="text-sm mx-2">
											{getPercentage(+getGroupValue(item), item)}%
										</span>
									{/key}
								</div>
							</footer>
						</div>
					{/each}
				</div>
			{:else if tabSet === 1}
				<div class="h-full grid grid-cols-1 sm:grid-cols-3 p-4">
					<h3 class="h3 hidden sm:flex text-center w-full col-span-1 sm:col-span-2 items-center justify-center border border-gray-50">Loot table</h3>
					<h3 class="h3 hidden sm:flex w-full items-center justify-center text-center border border-gray-50 h-full">
						Disc chance to be found
					</h3>
					<div class="h3 hidden sm:flex mb-4 col-span-3" />

					{#each Object.entries(value) as [key, val]}
						<h5 class="h5 p-2 sm:border-b sm:border-gray-600 col-span-1 sm:col-span-2">{key}</h5>
						<div class="input appearance-none flex items-baseline">
							<input
								type="number"
								class="input"
								min="0"
								max="1000"
								data-type="item"
								data-target={key}
								bind:value={value[key]}
								placeholder="0"
							/>
							<span class="text-sm mx-2">
								{getPercentage(value[key], keyToGroup(key))}%
							</span>
						</div>
					{/each}
				</div>
			{/if}

			<div class="flex w-full items-center justify-center">
				<button class="btn variant-filled-secondary text-white" on:click={exit}>Save</button>
			</div>
		</svelte:fragment>
	</TabGroup>
</main>

<style>
	input::-webkit-outer-spin-button,
	input::-webkit-inner-spin-button {
		-webkit-appearance: none;
		margin: 0;
	}
</style>
