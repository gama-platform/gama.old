/*
 * Copyright 2016 Clifton Labs Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package msi.gama.util.file.json;

/**
 * Jsonables can be serialized in java script object notation (JSON). Deserializing a String produced by a Jsonable
 * should represent the Jsonable in JSON form.
 *
 * @author A. Drogoul. Simplification and adaptation from json-simple
 *
 */
public interface Jsonable {
	/**
	 * Serialize to a JSON formatted string.
	 *
	 * @return a string, formatted in JSON, that represents the Jsonable.
	 */
	String toJson();

}
