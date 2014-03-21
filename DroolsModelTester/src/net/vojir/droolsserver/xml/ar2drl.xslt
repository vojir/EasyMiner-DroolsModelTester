<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ar="http://keg.vse.cz/lm/AssociationRules/v1.0" exclude-result-prefixes="ar" version="1.0">
    
    <xsl:output encoding="UTF-8" method="text" indent="no" xml:space="default" />
    
    <xsl:template match="/">
            <xsl:apply-templates select="/ar:AssociationRules/ar:AssociationRule" />
    </xsl:template>
    
    <xsl:template match="ar:AssociationRule">
        rule "rule_<xsl:value-of select="@id"/>"
            no-loop true
            when
                $ar:DrlAR(id=="") and
                (<xsl:apply-templates select="./ar:Antecedent" mode="drlCondition" />)
            then
            <!-- TODO: doplnění délky antecedentu -->
                DrlAR $thisAR=new DrlAR("rule_<xsl:value-of select="@id"/>",<xsl:value-of select="count(./ar:Antecedent//ar:Attribute)"/>,
                <!-- <xsl:value-of select="(./ar:IMValues/ar:IMValue[@name='FUI'])"/> -->
                <xsl:value-of select="(./ar:FourFtTable/@a div (./ar:FourFtTable/@a + ./ar:FourFtTable/@b))"/>
                ,<xsl:value-of select="(./ar:IMValues/ar:IMValue[@name='BASE'])"/>);
                if (isBetterAR($ar,$thisAR)){
                    $ar.updateFromAR($thisAR);
                    update($ar);   
                }   
         end
         rule "rule_<xsl:value-of select="@id"/>_consequent"
            when
                $ar:DrlAR(id=="rule_<xsl:value-of select="@id"/>") and
                (<xsl:apply-templates select="./ar:Consequent" mode="drlCondition" />)
            then
                $ar.setCheckedOk(true);          
         end
    </xsl:template>
    
    <xsl:template match="ar:Antecedent" mode="drlCondition">
        <xsl:apply-templates select="./ar:Cedent" />
        <xsl:apply-templates select="./ar:Attribute" />
    </xsl:template>
    
    <xsl:template match="ar:Consequent" mode="drlCondition">
        <xsl:apply-templates select="./ar:Cedent" />
        <xsl:apply-templates select="./ar:Attribute" />
    </xsl:template>
    
    <xsl:template match="ar:Cedent">
        <xsl:if test="@connective='Negation'">
            <!-- tady by to možná chtělo nějak  -->
            not  
        </xsl:if>
        <xsl:choose>
            <xsl:when test="count(./*)>1">
                (<xsl:apply-templates select="./*" />)
            </xsl:when>
            <xsl:otherwise><xsl:apply-templates select="./*" /></xsl:otherwise>
        </xsl:choose>
        
        <xsl:if test="position() != last()">
            <xsl:choose>
                <xsl:when test="parent::node()/@connective='Conjunction'"> and </xsl:when>
                <xsl:when test="parent::node()/@connective='Disjunction'"> or </xsl:when>
                <xsl:when test="not(parent::node()/@connective)"> and </xsl:when>
            </xsl:choose>
        </xsl:if> 
        
        <!--
        <xsl:choose>
            <xsl:when test="count(./Attribute)>0">
                <!- -jde o cedent složený z konkrétních atributů - ->
                <xsl:for-each select="./Attribute"> 
                    <xsl:apply-templates select="." />
                    <xsl:if test="position() != last()">
                        <xsl:choose>
                            <xsl:when test="parent::node()/@connective='Conjunction'"> and </xsl:when>
                            <xsl:when test="parent::node()/@connective='Disjunction'"> or </xsl:when>
                            <xsl:when test="parent::node()/@connective='Negation'"> not </xsl:when>
                        </xsl:choose>  
                    </xsl:if> 
                </xsl:for-each>
            </xsl:when>
            <xsl:when test="count(./Cedent)>0">
                <!- -jde o složený cedent- ->
                <xsl:for-each select="./Cedent">
                    (<xsl:apply-templates select="." />)
                    <xsl:if test="position() != last()">
                        <xsl:choose>
                            <xsl:when test="parent::node()/@connective='Conjunction'"> and </xsl:when>
                            <xsl:when test="parent::node()/@connective='Disjunction'"> or </xsl:when>
                            <xsl:when test="parent::node()/@connective='Negation'"> not </xsl:when>
                        </xsl:choose>
                    </xsl:if>    
                </xsl:for-each>
            </xsl:when>
        </xsl:choose>-->
    </xsl:template>
    
    <xsl:template match="ar:Category">
        <xsl:if test="position()>1"> || </xsl:if>
        <xsl:apply-templates select="ar:Data" />
    </xsl:template>
    
    <xsl:template match="ar:Attribute" xml:space="preserve" >
        DrlObj (name == "<xsl:value-of select="./ar:Column/text()"/>", 
        <!--
        <xsl:choose>
            <xsl:when test="count(./ar:Category)>0">
            
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="./ar:Category/ar:Data" />    
            </xsl:otherwise>
        </xsl:choose>-->
        <xsl:apply-templates select="ar:Category" />
        )
        <xsl:if test="position() != last()">
            <xsl:if test="../@connective='Conjunction'">
                and
            </xsl:if>
            <xsl:if test="../@connective='Disjunction'">
                or
            </xsl:if> 
            <xsl:if test="not(../@connective)">
                and
            </xsl:if> 
            <!--
            <xsl:choose>
                <xsl:when test="parent::node()/@connective='Conjunction'"> and </xsl:when>
                <xsl:when test="parent::node()/@connective='Disjunction'"> or </xsl:when>
            </xsl:choose>-->
        </xsl:if> 
    </xsl:template>
    
    <xsl:template match="ar:Data">
        <xsl:choose>
            <xsl:when test="count(./*)>1">(<xsl:apply-templates select="./*" />)</xsl:when>
            <xsl:otherwise><xsl:apply-templates select="./*" /></xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="ar:Data/ar:Interval">
        <xsl:if test="position()>1"> || </xsl:if>
        <xsl:choose>
            <xsl:when test="./@closure='closedClosed'">(numVal &gt;= <xsl:value-of select="@leftMargin"/> &amp;&amp; numVal &lt;= <xsl:value-of select="@rightMargin"/>)</xsl:when>
            <xsl:when test="./@closure='openClosed'">  (numVal &gt; <xsl:value-of select="@leftMargin"/>  &amp;&amp; numVal &lt;= <xsl:value-of select="@rightMargin"/>)</xsl:when>
            <xsl:when test="./@closure='closedOpen'">  (numVal &gt;= <xsl:value-of select="@leftMargin"/> &amp;&amp; numVal &lt; <xsl:value-of select="@rightMargin"/>)</xsl:when>
            <xsl:when test="./@closure='openOpen'">    (numVal &gt; <xsl:value-of select="@leftMargin"/>  &amp;&amp; numVal &lt; <xsl:value-of select="@rightMargin"/>)</xsl:when>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="ar:Data/ar:Value" xml:space="preserve"><xsl:if test="position()>1"> || </xsl:if> value == "<xsl:value-of select="text()"/>"</xsl:template>
</xsl:stylesheet>