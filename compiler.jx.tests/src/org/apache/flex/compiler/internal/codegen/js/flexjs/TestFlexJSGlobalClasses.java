/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.flex.compiler.internal.codegen.js.flexjs;

import org.apache.flex.compiler.driver.IBackend;
import org.apache.flex.compiler.internal.codegen.js.goog.TestGoogGlobalClasses;
import org.apache.flex.compiler.internal.driver.js.flexjs.FlexJSBackend;
import org.apache.flex.compiler.internal.driver.js.goog.JSGoogConfiguration;
import org.apache.flex.compiler.internal.projects.FlexJSProject;
import org.apache.flex.compiler.internal.tree.as.VariableNode;
import org.apache.flex.compiler.tree.as.IASNode;
import org.apache.flex.compiler.tree.as.IBinaryOperatorNode;
import org.apache.flex.compiler.tree.as.IForLoopNode;
import org.apache.flex.compiler.tree.as.IFunctionCallNode;
import org.apache.flex.compiler.tree.as.IFunctionNode;
import org.apache.flex.compiler.tree.as.IUnaryOperatorNode;
import org.apache.flex.compiler.tree.as.IVariableNode;
import org.junit.Test;
import org.junit.Ignore;

/**
 * @author Erik de Bruin
 */
public class TestFlexJSGlobalClasses extends TestGoogGlobalClasses
{
    @Override
    public void setUp()
    {
    	project = new FlexJSProject(workspace);
    	((FlexJSProject)project).config = new JSGoogConfiguration();
    	project.setProxyBaseClass("flash.utils.Proxy");
        super.setUp();
    }

    @Override
    protected IBackend createBackend()
    {
        return new FlexJSBackend();
    }

    @Override
    @Test
    public void testArguments()
    {
        IFunctionNode node = getMethod("function a():void {  trace(arguments);}");
        asBlockWalker.visitFunction(node);
        assertOut("FalconTest_A.prototype.a = function() {\n  org.apache.flex.utils.Language.trace(arguments);\n}");
    }

    @Test
    public void testArrayNoArgs()
    {
        IVariableNode node = getVariable("var a:Array = new Array();");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Array} */ a = new Array()");
    }

    @Test
    public void testArrayStringArgs()
    {
        IVariableNode node = getVariable("var a:Array = new Array('Hello', 'World');");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Array} */ a = new Array('Hello', 'World')");
    }

    @Test
    public void testArraySizeArg()
    {
        IVariableNode node = getVariable("var a:Array = new Array(30);");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Array} */ a = new Array(30)");
    }

    @Test
    public void testArrayNumberArgs()
    {
        IVariableNode node = getVariable("var a:Array = new Array(30, 40);");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Array} */ a = new Array(30, 40)");
    }

    @Test
    public void testArrayArrayArg()
    {
        IVariableNode node = getVariable("var a:Array = new Array(['Hello', 'World']);");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Array} */ a = new Array(['Hello', 'World'])");
    }

    @Test
    public void testArrayConstCaseInsensitive()
    {
        IVariableNode node = getVariable("var a:Number = Array.CASEINSENSITIVE");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {number} */ a = 1");
    }

    @Test
    public void testArrayConstNumeric()
    {
        IVariableNode node = getVariable("var a:Number = Array.NUMERIC");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {number} */ a = 16");
    }

    @Ignore
    public void testArrayRemoveAt()
    {
    	// requires FP19 or newer
        IBinaryOperatorNode node = getBinaryNode("var a:Array = new Array(); a.removeAt(2)");
        IFunctionCallNode parentNode = (IFunctionCallNode)(node.getParent());
        asBlockWalker.visitFunctionCall(parentNode);
        assertOut("a.splice(2, 1)");
    }

    @Ignore
    public void testArrayInsertAt()
    {
    	// requires FP19 or newer
        IBinaryOperatorNode node = getBinaryNode("var a:Array = new Array(); a.insertAt(2, 'foo')");
        IFunctionCallNode parentNode = (IFunctionCallNode)(node.getParent());
        asBlockWalker.visitFunctionCall(parentNode);
        assertOut("a.splice(2, 0, 'foo')");
    }

    @Test
    public void testArraySortOn()
    {
        IBinaryOperatorNode node = getBinaryNode("var a:Array = new Array(); a.sortOn('foo')");
        IFunctionCallNode parentNode = (IFunctionCallNode)(node.getParent());
        asBlockWalker.visitFunctionCall(parentNode);
        assertOut("org.apache.flex.utils.Language.sortOn(a, 'foo')");
    }

    @Test
    public void testArraySortOnTwoArgs()
    {
        IBinaryOperatorNode node = getBinaryNode("var a:Array = new Array(); a.sortOn('foo', 10)");
        IFunctionCallNode parentNode = (IFunctionCallNode)(node.getParent());
        asBlockWalker.visitFunctionCall(parentNode);
        assertOut("org.apache.flex.utils.Language.sortOn(a, 'foo', 10)");
    }

    @Test
    public void testIntConstMaxValue()
    {
        IVariableNode node = getVariable("var a:Number = int.MAX_VALUE");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {number} */ a = 2147483648");
    }

    @Test
    public void testIntConstMinValue()
    {
        IVariableNode node = getVariable("var a:Number = int.MIN_VALUE");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {number} */ a = -2147483648");
    }

    @Test
    public void testUintConstMaxValue()
    {
        IVariableNode node = getVariable("var a:Number = uint.MAX_VALUE");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {number} */ a = 4294967295");
    }

    @Test
    public void testUintConstMinValue()
    {
        IVariableNode node = getVariable("var a:Number = uint.MIN_VALUE");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {number} */ a = 0");
    }

    @Test
    public void testDateGetMinutes()
    {
        IVariableNode node = getVariable("var a:Date = new Date(); var b:Number = a.minutes");
        node = (IVariableNode)(node.getParent().getChild(1));
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {number} */ b = a.getMinutes()");
    }

    @Test
    public void testDateSetMinutes()
    {
    	IBinaryOperatorNode node = getBinaryNode("var a:Date = new Date(); a.minutes = 10");
        asBlockWalker.visitBinaryOperator(node);
        assertOut("a.setMinutes(10, a.getSeconds(), a.getMilliseconds())");
    }

    @Test
    public void testDateGetMinutesMethod()
    {
        IVariableNode node = getVariable("var a:Date = new Date(); var b:Number = a.getMinutes()");
        node = (IVariableNode)(node.getParent().getChild(1));
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {number} */ b = a.getMinutes()");
    }
    
    @Test
    public void testDateSetMinutesMethod()
    {
    	IBinaryOperatorNode node = getBinaryNode("var a:Date = new Date(); a.setMinutes(10, 0, 0)");
    	IFunctionCallNode parentNode = (IFunctionCallNode)(node.getParent());
        asBlockWalker.visitFunctionCall(parentNode);
        assertOut("a.setMinutes(10, 0, 0)");
    }

    @Override
    @Test
    public void testVector()
    {
        IVariableNode node = getVariable("var a:Vector.<String> = new Vector.<String>(['Hello', 'World']);");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Array} */ a = new Array(['Hello', 'World'])");
    }

    @Test
    public void testVectorLiteral_1()
    {
        IVariableNode node = getVariable("var a:Vector.<String> = new <String>[];");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Array} */ a = []");
    }

    @Test
    public void testVectorLiteral_2()
    {
        IVariableNode node = getVariable("var a:Vector.<int> = new <int>[0, 1, 2, 3];");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Array} */ a = [0, 1, 2, 3]");
    }

    @Test
    public void testVectorLiteral_3()
    {
        IVariableNode node = getVariable("var a:Vector.<String> = new <String>[\"one\", \"two\", \"three\";");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Array} */ a = [\"one\", \"two\", \"three\"]");
    }
    
    @Test
    public void testVectorNoArgs()
    {
        IVariableNode node = getVariable("var a:Vector.<String> = new Vector.<String>();");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Array} */ a = new Array()");
    }

    @Test
    public void testVectorStringArgs()
    {
        IVariableNode node = getVariable("var a:Vector.<String> = new Vector.<String>('Hello', 'World');");
        asBlockWalker.visitVariable(node);
        // expected error?
        assertOut("var /** @type {Array} */ a = new Array('Hello', 'World')");
    }

    @Test
    public void testVectorStringArgs3()
    {
        IVariableNode node = getVariable("var a:Vector.<String> = new Vector.<String>('Hello', 'World', 'Three');");
        asBlockWalker.visitVariable(node);
        // expected error?
        assertOut("var /** @type {Array} */ a = new Array('Hello', 'World', 'Three')");
    }

    @Test
    public void testVectorSizeArg()
    {
        IVariableNode node = getVariable("var a:Vector.<String> = new Vector.<String>(30);");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Array} */ a = new Array(30)");
    }

    @Test
    public void testVectorNumberArgs()
    {
        IVariableNode node = getVariable("var a:Vector.<String> = new Vector.<String>(30, 40);");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Array} */ a = new Array(30, 40)");
    }

    @Test
    public void testVectorArrayArg()
    {
        IVariableNode node = getVariable("var a:Vector.<String> = new Vector.<String>(['Hello', 'World']);");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Array} */ a = new Array(['Hello', 'World'])");
    }

    @Test
    public void testXML()
    {
        IVariableNode node = getVariable("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {XML} */ a = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\")");
    }
    
    @Test
    public void testXMLLiteral()
    {
        IVariableNode node = getVariable("var a:XML = <top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {XML} */ a = new XML( \"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\") ");
    }
    
    @Test
    public void testXMLLiteralWithTemplate()
    {
        VariableNode node = (VariableNode)getNode("private function get tagname():String { return 'name'; };\n" +
        							 "private function get attributename():String { return 'id'; };\n" +
        							 "private function get attributevalue():Number { return 5; };\n" +
        							 "private function get content():String { return 'Fred'; };\n" +
        							 "private function test() { var a:XML = <{tagname} {attributename}={attributevalue}>{content}</{tagname}>;}",
        							 VariableNode.class, WRAP_LEVEL_CLASS);
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {XML} */ a = new XML( '<' + this.tagname + ' ' + this.attributename + '=' + this.attributevalue + '>' + this.content + '</' + this.tagname + '>') ");
    }
    
    @Test
    public void testXMLLiteralAsParam()
    {
        IFunctionCallNode node = (IFunctionCallNode)getNode("var a:XML; a.appendChild(<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>)",
        		IFunctionCallNode.class);
        asBlockWalker.visitFunctionCall(node);
        assertOut("a.appendChild(new XML( \"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\") )");
    }
    
    @Test
    public void testXMLLiteralInReassign()
    {
    	IBinaryOperatorNode node = getBinaryNode("var a:XML = <foo />; a = <top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>)");
        asBlockWalker.visitBinaryOperator(node);
        assertOut("a = new XML( \"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\") ");
    }
    
    @Test
    public void testXMLSingleDot()
    {
        IVariableNode node = getVariable("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");var b:XMLList = a.child;");
        IASNode parentNode = node.getParent();
        node = (IVariableNode) parentNode.getChild(1);
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {XMLList} */ b = a.child('child')");
    }
    
    @Test
    public void testXMLSingleDotChain()
    {
        IVariableNode node = getVariable("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");var b:XMLList = a.child.grandchild;");
        IASNode parentNode = node.getParent();
        node = (IVariableNode) parentNode.getChild(1);
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {XMLList} */ b = a.child('child').child('grandchild')");
    }
    
    @Test
    public void testXMLDelete()
    {
        IUnaryOperatorNode node = getUnaryNode("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");delete a.child;");
        asBlockWalker.visitUnaryOperator(node);
        assertOut("a.removeChild('child')");
    }
    
    @Test
    public void testXMLDeleteChain()
    {
        IUnaryOperatorNode node = getUnaryNode("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");delete a.child.grandchild;");
        asBlockWalker.visitUnaryOperator(node);
        assertOut("a.child('child').removeChild('grandchild')");
    }

    @Test
    public void testXMLDeleteObjChain()
    {
        IUnaryOperatorNode node = getUnaryNode("public var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");private function foo() { delete this.a.child.grandchild;}",
        		WRAP_LEVEL_CLASS);
        asBlockWalker.visitUnaryOperator(node);
        assertOut("this.a.child('child').removeChild('grandchild')");
    }
    
    @Test
    public void testXMLDeleteCastChain()
    {
        IUnaryOperatorNode node = getUnaryNode("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");var b:Object = { xml: a};delete XML(b.xml).child.grandchild;");
        asBlockWalker.visitUnaryOperator(node);
        assertOut("org.apache.flex.utils.Language.as(b.xml, XML, true).child('child').removeChild('grandchild')");
    }
    
    @Test
    public void testXMLDeleteCastAsChain()
    {
        IUnaryOperatorNode node = getUnaryNode("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");var b:Object = { xml: a};delete (b.xml as XML).child.grandchild;");
        asBlockWalker.visitUnaryOperator(node);
        assertOut("org.apache.flex.utils.Language.as(b.xml, XML).child('child').removeChild('grandchild')");
    }    

    @Test
    public void testXMLListDelete()
    {
        IUnaryOperatorNode node = getUnaryNode("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");delete a.child[0];");
        asBlockWalker.visitUnaryOperator(node);
        assertOut("a.child('child').removeChildAt(0)");
    }
    
    @Test
    public void testXMLListChain()
    {
        IUnaryOperatorNode node = getUnaryNode("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");delete a.child.grandchild[0];");
        asBlockWalker.visitUnaryOperator(node);
        assertOut("a.child('child').child('grandchild').removeChildAt(0)");
    }
    
    @Test
    public void testXMLListObjChain()
    {
        IUnaryOperatorNode node = getUnaryNode("public var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");private function foo() { delete this.a.child.grandchild[0];}",
        		WRAP_LEVEL_CLASS);
        asBlockWalker.visitUnaryOperator(node);
        assertOut("this.a.child('child').child('grandchild').removeChildAt(0)");
    }
    
    @Test
    public void testXMLListCastChain()
    {
        IUnaryOperatorNode node = getUnaryNode("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");var b:Object = { xml: a};delete XML(b.xml).child.grandchild[0];");
        asBlockWalker.visitUnaryOperator(node);
        assertOut("org.apache.flex.utils.Language.as(b.xml, XML, true).child('child').child('grandchild').removeChildAt(0)");
    }
    
    @Test
    public void testXMLListAsCastChain()
    {
        IUnaryOperatorNode node = getUnaryNode("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");var b:Object = { xml: a};delete (b.xml as XML).child.grandchild[0];");
        asBlockWalker.visitUnaryOperator(node);
        assertOut("org.apache.flex.utils.Language.as(b.xml, XML).child('child').child('grandchild').removeChildAt(0)");
    }
    
    @Test
    public void testXMLNameFunction()
    {
    	IVariableNode node = getVariable("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");var b:String = a.name();");
        IASNode parentNode = node.getParent();
        node = (IVariableNode) parentNode.getChild(1);
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {string} */ b = a.name()");
    }
    
    @Test
    public void testXMLListLengthFunction()
    {
    	IVariableNode node = getVariable("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");var b:int = a.child.length();");
        IASNode parentNode = node.getParent();
        node = (IVariableNode) parentNode.getChild(1);
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {number} */ b = a.child('child').length()");
    }
    
    @Test
    public void testXMLDoubleDot()
    {
        IVariableNode node = getVariable("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");var b:XMLList = a..child;");
        IASNode parentNode = node.getParent();
        node = (IVariableNode) parentNode.getChild(1);
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {XMLList} */ b = a.descendants('child')");
    }
    
    @Test
    public void testXMLAttribute()
    {
        IVariableNode node = getVariable("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");var b:XMLList = a.@attr1;");
        IASNode parentNode = node.getParent();
        node = (IVariableNode) parentNode.getChild(1);
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {XMLList} */ b = a.attribute('attr1')");
    }
    
    @Test
    public void testXMLFilter()
    {
        IVariableNode node = getVariable("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");var b:XMLList = a..grandchild.(@attr2 == 'fish');");
        IASNode parentNode = node.getParent();
        node = (IVariableNode) parentNode.getChild(1);
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {XMLList} */ b = a.descendants('grandchild').filter(function(node){return (node.attribute('attr2') == 'fish')})");
    }
    
    @Test
    public void testXMLSetAttribute()
    {
        IBinaryOperatorNode node = getBinaryNode("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");a.@bar = 'foo'");
        asBlockWalker.visitBinaryOperator(node);
        assertOut("a.setAttribute('bar', 'foo')");
    }
    
    @Test
    public void testXMLSetChild()
    {
        IBinaryOperatorNode node = getBinaryNode("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");a.foo = a.child");
        asBlockWalker.visitBinaryOperator(node);
        assertOut("a.setChild('foo', a.child('child'))");
    }
    
    @Test
    public void testXMLListConcat()
    {
        IBinaryOperatorNode node = getBinaryNode("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");a.foo += a.child");
        asBlockWalker.visitBinaryOperator(node);
        assertOut("a.child('foo').concat(a.child('child'))");
    }
    
    @Test
    public void testXMLListAddAndAssign()
    {
        IBinaryOperatorNode node = getBinaryNode("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");a.foo = a.child + a..grandchild");
        asBlockWalker.visitBinaryOperator(node);
        assertOut("a.setChild('foo', a.child('child').copy().concat(a.descendants('grandchild')))");
    }
    
    @Test
    public void testXMLForLoop()
    {
        IForLoopNode node = getForLoopNode("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");for (var p:* in a) delete a[p];");
        asBlockWalker.visitForLoop(node);
        assertOut("for (var /** @type {*} */ p in a.elementNames())\n  a.removeChild(p);");
    }
    
    @Test
    public void testXMLForEachLoop()
    {
    	IForLoopNode node = getForLoopNode("var a:XML = new XML(\"<top attr1='cat'><child attr2='dog'><grandchild attr3='fish'>text</grandchild></child></top>\");for each (var p:XMLList in a) var i:int = p.length();");
        asBlockWalker.visitForLoop(node);
        assertOut("var foreachiter0_target = a;\nfor (var foreachiter0 in foreachiter0_target.elementNames()) \n{\nvar p = foreachiter0_target.child(foreachiter0);\n\n  var /** @type {number} */ i = p.length();}\n");
    }
    
    @Test
    public void testNamespaceNoArg()
    {
        IVariableNode node = getVariable("var a:Namespace = new Namespace();");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Namespace} */ a = new Namespace()");
    }
    
    @Test
    public void testNamespaceOneArg()
    {
        IVariableNode node = getVariable("var a:Namespace = new Namespace('foo');");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Namespace} */ a = new Namespace('foo')");
    }
    
    @Test
    public void testNamespaceTwoArg()
    {
        IVariableNode node = getVariable("var a:Namespace = new Namespace('foo', 'bar');");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {Namespace} */ a = new Namespace('foo', 'bar')");
    }
        
    @Test
    public void testQNameNoArg()
    {
        IVariableNode node = getVariable("var a:QName = new QName();");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {QName} */ a = new QName()");
    }
    
    @Test
    public void testQNameTwoArg()
    {
        IVariableNode node = getVariable("var a:QName = new QName(new Namespace('foo'), 'bar');");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {QName} */ a = new QName(new Namespace('foo'), 'bar')");
    }

    @Test
    public void testQNameOneArg()
    {
        IVariableNode node = getVariable("var a:QName = new QName(new QName(new Namespace('foo'), 'bar'));");
        asBlockWalker.visitVariable(node);
        assertOut("var /** @type {QName} */ a = new QName(new QName(new Namespace('foo'), 'bar'))");
    }
    
    
    @Test
    public void testProxy()
    {
        IFunctionNode node = (IFunctionNode) getNode(
                "import flash.utils.Proxy; public class B {public function b() { var a:Proxy = new Proxy();a.foo = 'bar'; }}",
                IFunctionNode.class, WRAP_LEVEL_PACKAGE, true);
        asBlockWalker.visitFunction(node);
        assertOut("/**\n * @export\n */\nfoo.bar.B.prototype.b = function() {\n  var /** @type {flash.utils.Proxy} */ a = new flash.utils.Proxy();\n  a.setProperty('foo', 'bar');\n}");
    }
    
    @Test
    public void testProxyGet()
    {
        IFunctionNode node = (IFunctionNode) getNode(
                "import flash.utils.Proxy; public class B {public function b() { var a:Proxy = new Proxy();var bar:* = a.foo; }}",
                IFunctionNode.class, WRAP_LEVEL_PACKAGE, true);
        asBlockWalker.visitFunction(node);
        assertOut("/**\n * @export\n */\nfoo.bar.B.prototype.b = function() {\n  var /** @type {flash.utils.Proxy} */ a = new flash.utils.Proxy();\n  var /** @type {*} */ bar = a.getProperty('foo');\n}");
    }
    
    @Test
    public void testProxyConcat()
    {
        IFunctionNode node = (IFunctionNode) getNode(
                "import flash.utils.Proxy; public class B {public function b() { var a:Proxy = new Proxy();var baz:String = a.foo + 'bar'; }}",
                IFunctionNode.class, WRAP_LEVEL_PACKAGE, true);
        asBlockWalker.visitFunction(node);
        assertOut("/**\n * @export\n */\nfoo.bar.B.prototype.b = function() {\n  var /** @type {flash.utils.Proxy} */ a = new flash.utils.Proxy();\n  var /** @type {string} */ baz = a.getProperty('foo') + 'bar';\n}");
    }
    
    @Test
    public void testProxyAddAndAssign()
    {
        IFunctionNode node = (IFunctionNode) getNode(
                "import flash.utils.Proxy; public class B {public function b() { var a:Proxy = new Proxy();a.foo += 'bar'; }}",
                IFunctionNode.class, WRAP_LEVEL_PACKAGE, true);
        asBlockWalker.visitFunction(node);
        assertOut("/**\n * @export\n */\nfoo.bar.B.prototype.b = function() {\n  var /** @type {flash.utils.Proxy} */ a = new flash.utils.Proxy();\n  a.setProperty('foo', a.getProperty('foo') + 'bar');\n}");
    }
    
    @Test
    public void testProxyForLoop()
    {
    	IForLoopNode node = (IForLoopNode) getNode(
                "import flash.utils.Proxy; public class B {public function b() { var a:Proxy = new Proxy();for (var p:* in a) delete a[p];; }}",
                IForLoopNode.class, WRAP_LEVEL_PACKAGE, true);
        asBlockWalker.visitForLoop(node);
        assertOut("for (var /** @type {*} */ p in a.propertyNames())\n  a.deleteProperty(p);");
    }
    
    @Test
    public void testProxyForEachLoop()
    {
    	IForLoopNode node = (IForLoopNode) getNode(
                "import flash.utils.Proxy; public class B {public function b() { var a:Proxy = new Proxy();for each (var p:String in a) var i:int = p.length; }}",
                IForLoopNode.class, WRAP_LEVEL_PACKAGE, true);
        asBlockWalker.visitForLoop(node);
        assertOut("var foreachiter0_target = a;\nfor (var foreachiter0 in foreachiter0_target.propertyNames()) \n{\nvar p = foreachiter0_target.getProperty(foreachiter0);\n\n  var /** @type {number} */ i = p.length;}\n");
    }
    
}
