#!/usr/bin/env python3
"""
Convert datagen output from 1.21.1 format to 1.20.1 format.

Handles:
- Directory renaming (recipe->recipes, loot_table->loot_tables, etc.)
- Recipe JSON conversion (result format, show_notification)
- Stonecutting recipe conversion (flatten result object)
- Advancement JSON conversion (items format, sends_telemetry_event)
- Tag JSON conversion (add replace field)
- Assets are copied as-is (blockstates, models are identical)
"""

import argparse
import json
import os
import shutil
import sys

# Directory name mappings (1.21.1 -> 1.20.1)
DIR_RENAMES = {
    'recipe': 'recipes',
    'loot_table': 'loot_tables',
    'advancement': 'advancements',
}

# Tag subdirectory renames
TAG_DIR_RENAMES = {
    'block': 'blocks',
}


def convert_shaped_recipe(data):
    """Convert shaped/shapeless crafting recipe from 1.21.1 to 1.20.1 format."""
    if 'result' in data and isinstance(data['result'], dict):
        result = data['result']
        if 'id' in result:
            result['item'] = result.pop('id')
    data['show_notification'] = True
    return data


def convert_stonecutting_recipe(data):
    """Convert stonecutting recipe from 1.21.1 to 1.20.1 format."""
    if 'result' in data and isinstance(data['result'], dict):
        result = data['result']
        count = result.get('count', 1)
        result_id = result.get('id', '')
        data['result'] = result_id
        data['count'] = count
        # Reorder: type, count, ingredient, result
        reordered = {}
        if 'type' in data:
            reordered['type'] = data['type']
        reordered['count'] = data['count']
        if 'ingredient' in data:
            reordered['ingredient'] = data['ingredient']
        reordered['result'] = data['result']
        return reordered
    return data


def convert_recipe(data):
    """Convert recipe JSON based on type."""
    recipe_type = data.get('type', '')
    if recipe_type == 'minecraft:stonecutting':
        return convert_stonecutting_recipe(data)
    else:
        return convert_shaped_recipe(data)


def convert_advancement(data):
    """Convert advancement JSON from 1.21.1 to 1.20.1 format."""
    # Convert items from string to array in criteria conditions
    if 'criteria' in data:
        for criterion in data['criteria'].values():
            if 'conditions' in criterion and 'items' in criterion['conditions']:
                items = criterion['conditions']['items']
                if isinstance(items, list):
                    for item_entry in items:
                        if isinstance(item_entry, dict) and 'items' in item_entry:
                            if isinstance(item_entry['items'], str):
                                item_entry['items'] = [item_entry['items']]

    # Sort requirements sublists for consistent ordering
    if 'requirements' in data:
        for req_list in data['requirements']:
            if isinstance(req_list, list):
                req_list.sort()

    # Add sends_telemetry_event
    data['sends_telemetry_event'] = False
    return data


def convert_tag(data):
    """Convert tag JSON from 1.21.1 to 1.20.1 format."""
    if 'replace' not in data:
        # Insert replace at the beginning
        new_data = {'replace': False}
        new_data.update(data)
        return new_data
    return data


def process_file(src_path, dst_path, converter=None):
    """Process a single file, optionally applying a converter."""
    os.makedirs(os.path.dirname(dst_path), exist_ok=True)

    if converter and src_path.endswith('.json'):
        with open(src_path, 'r') as f:
            data = json.load(f)
        data = converter(data)
        with open(dst_path, 'w') as f:
            json.dump(data, f, indent=2)
            f.write('\n')
    else:
        shutil.copy2(src_path, dst_path)


def convert_data_dir(src_base, dst_base):
    """Convert data directory with renames and JSON transformations."""
    for root, dirs, files in os.walk(src_base):
        rel_path = os.path.relpath(root, src_base)
        parts = rel_path.split(os.sep)

        # Apply directory renames
        new_parts = []
        for i, part in enumerate(parts):
            if part in DIR_RENAMES:
                new_parts.append(DIR_RENAMES[part])
            elif i > 0 and parts[i - 1] == 'tags' and part in TAG_DIR_RENAMES:
                new_parts.append(TAG_DIR_RENAMES[part])
            else:
                new_parts.append(part)

        new_rel_path = os.sep.join(new_parts)

        for filename in files:
            src_path = os.path.join(root, filename)
            dst_path = os.path.join(dst_base, new_rel_path, filename)

            # Determine converter based on directory
            converter = None
            if any(p == 'recipe' for p in parts):
                converter = convert_recipe
            elif any(p == 'advancement' for p in parts):
                converter = convert_advancement
            elif any(p == 'tags' for p in parts):
                converter = convert_tag
            # loot_table: no content conversion needed

            process_file(src_path, dst_path, converter)


def main():
    parser = argparse.ArgumentParser(
        description='Convert datagen output from 1.21.1 to 1.20.1 format')
    parser.add_argument('input_dir', help='1.21.1 generated directory')
    parser.add_argument('output_dir', help='Output directory for 1.20.1 format')
    args = parser.parse_args()

    if not os.path.isdir(args.input_dir):
        print(f"Error: Input directory does not exist: {args.input_dir}",
              file=sys.stderr)
        sys.exit(1)

    # Clean output directory
    if os.path.exists(args.output_dir):
        shutil.rmtree(args.output_dir)

    assets_src = os.path.join(args.input_dir, 'assets')
    assets_dst = os.path.join(args.output_dir, 'assets')
    data_src = os.path.join(args.input_dir, 'data')
    data_dst = os.path.join(args.output_dir, 'data')

    file_count = 0

    # Copy assets as-is
    if os.path.isdir(assets_src):
        shutil.copytree(assets_src, assets_dst)
        for root, dirs, files in os.walk(assets_dst):
            file_count += len(files)

    # Convert data
    if os.path.isdir(data_src):
        convert_data_dir(data_src, data_dst)
        for root, dirs, files in os.walk(data_dst):
            file_count += len(files)

    # Copy .cache if exists
    cache_src = os.path.join(args.input_dir, '.cache')
    if os.path.isdir(cache_src):
        shutil.copytree(cache_src, os.path.join(args.output_dir, '.cache'))

    print(f"Converted {file_count} files from 1.21.1 to 1.20.1 format")


if __name__ == '__main__':
    main()
